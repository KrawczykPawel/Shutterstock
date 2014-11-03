/*
 * Copyright (C) 2012 The Andrino Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.krawczyk.shutterstocktask.model;

/**
 * @author Paweł Krawczyk
 * @class The ImageFile Class.
 * <p/>
 * @since 31 oct, 2014.
 */
public class ImageFile {
    private String mName;
    private String mThumbnailUrl;

    private String mUrl;

    public ImageFile(String name, String thumbnailUrl, String url) {
        mName = name;
        mThumbnailUrl = thumbnailUrl;
        mUrl = url;
    }

    public String getThumbnailUrl() {
        return mThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        mThumbnailUrl = thumbnailUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
