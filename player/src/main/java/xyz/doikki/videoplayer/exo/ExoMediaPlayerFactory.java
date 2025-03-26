package xyz.doikki.videoplayer.exo;

import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;

import xyz.doikki.videoplayer.player.PlayerFactory;

public class ExoMediaPlayerFactory extends PlayerFactory<ExoMediaPlayer> {
    public static ExoMediaPlayerFactory create() {
        return new ExoMediaPlayerFactory();
    }


    @OptIn(markerClass = UnstableApi.class) @Override
    public ExoMediaPlayer createPlayer(Context context) {
        return new ExoMediaPlayer(context);
    }
}
