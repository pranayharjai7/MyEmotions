package com.pranayharjai7.myemotions.Utils.Interfaces;

import com.pranayharjai7.myemotions.Database.Emotion;

import java.util.List;

/**
 * This interface is used as a callback to getRealtimeEmotionsForSyncing because
 * addListenerForSingleValueEvent() is asynchronous and function might return without any data.
 */
public interface OnRealtimeEmotionsLoadedCallback {
    void onRealtimeEmotionsLoaded(List<Emotion> emotions);
}
