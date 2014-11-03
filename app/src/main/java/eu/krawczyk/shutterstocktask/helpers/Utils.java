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

package eu.krawczyk.shutterstocktask.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import eu.krawczyk.shutterstocktask.ShutterstockTask;
import eu.krawczyk.shutterstocktask.model.ImageFile;

/**
 * @author Paweł Krawczyk
 * @class The Utils Class.
 * <p/>
 * @since 31 oct, 2014 Class for utils and helper methods.
 */
public class Utils {
    // In order to not create instance of this class
    private Utils() {
    }

    // Multiply ImageFile list for given amount of loops.
    public static List<ImageFile> multiplyList(List<ImageFile> listToDuplicate, int loops) {
        List<ImageFile> clonedList = new ArrayList<ImageFile>(listToDuplicate.size() * loops);
        for (int i = 0; i < loops; i++) {
            for (ImageFile item : listToDuplicate) {
                clonedList.add(item);
            }
        }
        return clonedList;
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) ShutterstockTask.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
