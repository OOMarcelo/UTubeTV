package com.sickboots.sickvideos.content;

import android.app.Fragment;
import android.content.Context;

import com.google.common.collect.ImmutableMap;
import com.sickboots.sickvideos.database.YouTubeData;
import com.sickboots.sickvideos.imageutils.ToolbarIcons;
import com.sickboots.sickvideos.mainactivity.ChannelAboutFragment;
import com.sickboots.sickvideos.mainactivity.YouTubeGridFragment;
import com.sickboots.sickvideos.services.YouTubeServiceRequest;
import com.sickboots.sickvideos.youtube.YouTubeAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class Content extends Observable {
  public static final String CONTENT_UPDATED_NOTIFICATION = "CONTENT_UPDATED";
  private Context mContext;
  public ChannelList mChannelList;

  public Content(Context context, List<ChannelList.ChannelCode> channelCodes) {
    super();

    mChannelList = new ChannelList(context, channelCodes, new ChannelList.OnChannelListUpdateListener() {
      @Override
      public void onUpdate() {
        notifyForDataUpdate();
      }
    });

    mContext = context.getApplicationContext();
  }

  public ArrayList<Map> drawerTitles() {
    ArrayList<Map> result = new ArrayList<Map>();

    result.add(ImmutableMap.of("title", "About", "icon", ToolbarIcons.IconID.ABOUT));
    result.add(ImmutableMap.of("title", "Playlists", "icon", ToolbarIcons.IconID.PLAYLISTS));
    result.add(ImmutableMap.of("title", "Recent Uploads", "icon", ToolbarIcons.IconID.UPLOADS));

    return result;
  }

  // returns false if that channel is already current
  public boolean changeChannel(int index) {
    if (mChannelList.changeChannel(index)) {
      return true;
    }
    return false;
  }

  public Fragment fragmentForIndex(int index) {
    Fragment fragment = null;

    switch (index) {
      case 0:
        fragment = new ChannelAboutFragment();
        break;
      case 1:
        fragment = YouTubeGridFragment.newInstance(YouTubeServiceRequest.playlistsRequest(mChannelList
            .currentChannelId(), null, 250));
        break;
      case 2:
        fragment = YouTubeGridFragment.newInstance(YouTubeServiceRequest.relatedRequest(YouTubeAPI.RelatedPlaylistType.UPLOADS, mChannelList
            .currentChannelId(), null, 50));
        break;
    }


    if (fragment != null)
      mChannelList.saveSectionIndex(index);

    return fragment;
  }

  public boolean needsChannelSwitcher() {
    return mChannelList.needsChannelSwitcher();
  }

  private void notifyForDataUpdate() {
    setChanged();
    notifyObservers(Content.CONTENT_UPDATED_NOTIFICATION);
  }

  public YouTubeData channelInfo() {
    return mChannelList.currentChannelInfo();
  }

  public String channelName() {
    String result = null;

    YouTubeData data = channelInfo();
    if (data != null)
      result = data.mTitle;

    return result;
  }

  public void refreshChannelInfo() {
    mChannelList.refresh();
  }

}
