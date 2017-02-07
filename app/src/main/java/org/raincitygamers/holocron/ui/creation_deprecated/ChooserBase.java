package org.raincitygamers.holocron.ui.creation_deprecated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.raincitygamers.holocron.ui.ActivityBase;

public class ChooserBase extends ActivityBase {
  public static final String ACTION_FINISH = "org.raincitygamers.holocron.ui.ActivityBase";
  private FinishReceiver finishReceiver;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    finishReceiver = new FinishReceiver();
    registerReceiver(finishReceiver, new IntentFilter(ACTION_FINISH));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(finishReceiver);
  }

  private final class FinishReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(ACTION_FINISH)) {
        finish();
      }
    }
  }
}
