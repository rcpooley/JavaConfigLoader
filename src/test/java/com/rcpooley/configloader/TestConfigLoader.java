package com.rcpooley.configloader;

import com.rcpooley.configloader.model.BasicUsage;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestConfigLoader {
    @Test
    public void testBasicUsage() throws ConfigException {
        BasicUsage config = ConfigLoader.loadJSON(
                TestConfigLoader.class.getResourceAsStream("/basicUsage.json"),
                BasicUsage.class
        );
        Assert.assertEquals("this is a string", config.getaString());
        Assert.assertEquals(1234, config.getbInt());
        Assert.assertEquals("this is a nested string", config.getcNested().getNestedString());
        Assert.assertEquals(2345, config.getcNested().getNestedInt());
        List<String> dList = config.getdList();
        Assert.assertEquals(3, dList.size());
        Assert.assertEquals("a", dList.get(0));
        Assert.assertEquals("b", dList.get(1));
        Assert.assertEquals("c", dList.get(2));
        ArrayList<Long> eList = config.geteList();
        Assert.assertEquals(3, eList.size());
        Assert.assertEquals(Long.valueOf(3), eList.get(0));
        Assert.assertEquals(Long.valueOf(2), eList.get(1));
        Assert.assertEquals(Long.valueOf(1992), eList.get(2));
    }
}
