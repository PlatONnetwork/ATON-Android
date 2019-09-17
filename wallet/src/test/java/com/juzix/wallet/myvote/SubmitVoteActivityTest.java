package com.juzix.wallet.myvote;


import android.content.Intent;
import com.juzix.wallet.BaseRobolectricTestCase;
import com.juzix.wallet.RobolectricApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, application = RobolectricApp.class)
public class SubmitVoteActivityTest  extends BaseRobolectricTestCase {
//
//    /**
//     * 验证intent参数传递
//     * @throws Exception
//     */
//    @Test
//    public void testStartActivityWithIntent() throws Exception {
//        Intent intent = new Intent();
//        intent.putExtra("extra_candidate_id", "5465z68sd4AS5d");
//        intent.putExtra("extra_candidate_name", "liyanzhu");
//        intent.putExtra("extra_candidate_deposit", "1111111");
//        SubmitVoteActivity submitVoteActivity = Robolectric.setupActivity(SubmitVoteActivity.class);
//        String extras = submitVoteActivity.getIntent().getStringExtra("extra_candidate_name");
//        assertNotNull(extras);
//        assertEquals("liyanzhu", extras);
//
//    }
}
