/*
 * Copyright (c) 2015 Hannes Dorfmann.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.zebdar.tom.sdk.adapter;

import android.support.annotation.NonNull;
import android.support.v4.util.SparseArrayCompat;
import android.view.ViewGroup;


/**
 */
public class AdapterDelegatesManager<T extends ItemBean> {

  /**
   * This id is used internally to claim that the {@link}
   */
  static final int FALLBACK_DELEGATE_VIEW_TYPE = Integer.MAX_VALUE - 1;

  /**
   * Map for ViewType to AdapterDeleage
   */
  SparseArrayCompat<AyoItemTemplate<T>> delegates = new SparseArrayCompat();

  /**
   *
   */
  public AdapterDelegatesManager<T> addDelegate(@NonNull AyoItemTemplate<T> delegate) {
    // algorithm could be improved since there could be holes,
    // but it's very unlikely that we reach Integer.MAX_VALUE and run out of unused indexes
    int viewType = delegates.size();
    while (delegates.get(viewType) != null) {
      viewType++;
      if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
        throw new IllegalArgumentException(
            "Oops, we are very close to Integer.MAX_VALUE. It seems that there are no more free and unused view type integers left to add another AdapterDelegate.");
      }
    }
    return addDelegate(viewType, false, delegate);
  }

  public AdapterDelegatesManager<T> addDelegate(int viewType,
      @NonNull AyoItemTemplate<T> delegate) {
    return addDelegate(viewType, false, delegate);
  }

  public AdapterDelegatesManager<T> addDelegate(int viewType, boolean allowReplacingDelegate,
      @NonNull AyoItemTemplate<T> delegate) {

    if (delegate == null) {
      throw new NullPointerException("AdapterDelegate is null!");
    }

    if (viewType == FALLBACK_DELEGATE_VIEW_TYPE) {
      throw new IllegalArgumentException("The view type = "
          + FALLBACK_DELEGATE_VIEW_TYPE
          + " is reserved for fallback adapter delegate (see setFallbackDelegate() ). Please use another view type.");
    }

    if (!allowReplacingDelegate && delegates.get(viewType) != null) {
      throw new IllegalArgumentException(
          "An AdapterDelegate is already registered for the viewType = "
              + viewType
              + ". Already registered AdapterDelegate is "
              + delegates.get(viewType));
    }

    delegates.put(viewType, delegate);

    return this;
  }

  public AdapterDelegatesManager<T> removeDelegate(@NonNull AyoItemTemplate<T> delegate) {

    if (delegate == null) {
      throw new NullPointerException("AdapterDelegate is null");
    }

    int indexToRemove = delegates.indexOfValue(delegate);

    if (indexToRemove >= 0) {
      delegates.removeAt(indexToRemove);
    }
    return this;
  }

  public AdapterDelegatesManager<T> removeDelegate(int viewType) {
    delegates.remove(viewType);
    return this;
  }

  public int getItemViewType(@NonNull T items, int position) {

    if (items == null) {
      throw new NullPointerException("Items datasource is null!");
    }

    int delegatesCount = delegates.size();
    for (int i = 0; i < delegatesCount; i++) {
      AyoItemTemplate<T> delegate = delegates.valueAt(i);
      if (delegate.isForViewType(items, position)) {
        return delegates.keyAt(i);
      }
    }

    throw new IllegalArgumentException(
        "No AdapterDelegate added that matches position=" + position + " in data source");
  }

  @NonNull
  public AyoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    AyoItemTemplate<T> delegate = delegates.get(viewType);
    if (delegate == null) {
      throw new NullPointerException("No AdapterDelegate added for ViewType " + viewType);
    }

    AyoViewHolder vh = delegate.onCreateViewHolder(parent);
    if (vh == null) {
      throw new NullPointerException("ViewHolder returned from AdapterDelegate "
          + delegate
          + " for ViewType ="
          + viewType
          + " is null!");
    }
    return vh;
  }

  public void onBindViewHolder(@NonNull T items, int position,
                               @NonNull AyoViewHolder viewHolder) {

    AyoItemTemplate<T> delegate = delegates.get(viewHolder.getItemViewType());
    if (delegate == null) {
      throw new NullPointerException(
              "No AdapterDelegate added for ViewType " + viewHolder.getItemViewType());
    }

    delegate.onBindViewHolder(items, position, viewHolder);
  }


  public int getViewType(@NonNull AyoItemTemplate<T> delegate) {
    if (delegate == null) {
      throw new NullPointerException("Delegate is null");
    }

    int index = delegates.indexOfValue(delegate);
    if (index == -1) {
      return -1;
    }
    return delegates.keyAt(index);
  }
}
