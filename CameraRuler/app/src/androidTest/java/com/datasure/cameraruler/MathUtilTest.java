package com.datasure.cameraruler;

import android.test.InstrumentationTestCase;

import com.datasure.util.MathUtil;

/**
 * Created by Lids on 2016/4/7.
 */
public class MathUtilTest extends InstrumentationTestCase{

    private MathUtil util;

    @Override
    public void setUp() throws Exception {
        util = MathUtil.getInstance();
    }


    public void testGetFormatString(){

        String str = null;
        str = util.getFormatString(0.01);
        assertEquals("%.2f", str);


    }


    @Override
    public void tearDown() throws Exception{
        util.destory();
    }
}
