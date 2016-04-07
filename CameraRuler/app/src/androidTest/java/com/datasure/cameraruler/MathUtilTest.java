package com.datasure.cameraruler;

import android.test.InstrumentationTestCase;

import com.datasure.com.datasure.util.MathUtil;

/**
 * Created by Lids on 2016/4/7.
 */
public class MathUtilTest extends InstrumentationTestCase{

    private MathUtil util;

    @Override
    public void setUp() throws Exception {
        util = MathUtil.getInstance();
    }

    public void test() throws Exception {

        assertEquals(1.5, util.calDistance(Math.PI/4));

    }

    @Override
    public void tearDown() throws Exception{
        util.destory();
    }
}
