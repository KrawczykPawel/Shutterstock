Shutterstock
=======

0. Instructions:

In order to download image tap on corresponding thumbnail
In order to see current download progress, see notification
In order to cancel download, choose "Cancel" action in corresponding notification

1. Why I use this Libraries and how I done it:

NETWORK:

While choosing network operations framework, I was basing on my experience with Android Volley framework, which fits my needs for this application:

See more: https://github.com/mcxiaoke/android-volley

LAYOUT:

I use RecyclerView, which was introduced in Anroid L combined with GridLayoutManager in order to increase the performance. Using this pattern we avoid to call several times "findById" method to get the UI widget reference making the ListView scrolling smoother.

DISPLAYING THUMBNAILS:

I use NetworkImageView from volley library and ImageLoader with LruCache in order to cache images and increase the performance.

SERVICE:

I use IntentService, because this service is no longer needed, after image was downloaded. It communicates with activity using broadcast receivers.





