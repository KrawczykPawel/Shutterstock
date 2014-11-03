/*
* Copyright (C) 2014 Paweł Krawczyk
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package eu.krawczyk.shutterstocktask.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import eu.krawczyk.shutterstocktask.R;
import eu.krawczyk.shutterstocktask.model.ImageFile;

/**
 * @author Paweł Krawczyk
 * @class The ImageAdapter Class.
 * <p/>
 * @since 31 oct, 2014 Adapter for image displaying.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private ImageLoader mImageLoader;

    private List<ImageFile> mDataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public NetworkImageView mImageView;

        public ViewHolder(NetworkImageView iv) {
            super(iv);
            mImageView = iv;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ImageAdapter(List<ImageFile> myDataset, ImageLoader imageLoader) {
        mDataSet = myDataset;
        mImageLoader = imageLoader;
    }

    public void updateDataSet(List<ImageFile> myDataset) {
        mDataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        NetworkImageView v = (NetworkImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        ImageFile item = mDataSet.get(position);
        String imageUrl = item.getThumbnailUrl();
        holder.mImageView.setImageUrl(imageUrl, mImageLoader);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}
