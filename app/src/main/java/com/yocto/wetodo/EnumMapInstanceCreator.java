package com.yocto.wetodo;

import com.google.gson.InstanceCreator;

import java.util.EnumMap;

/**
 * Created by yccheok on 7/5/2017.
 */

// http://stackoverflow.com/questions/16127904/gson-fromjson-return-linkedhashmap-instead-of-enummap
// Note, gson-2.1 from Google Drive google-api-services-drive-v2-rev62-1.13.2-beta
// doesn't give us expected result. We need to use gson-2.2.3. We are not sure
// whether this will cause any trouble for Google Drive API. We have to perform
// intensive testing to figure out.
public class EnumMapInstanceCreator<K extends Enum<K>, V> implements InstanceCreator<EnumMap<K, V>> {
    private final Class<K> enumClazz;

    public EnumMapInstanceCreator(final Class<K> enumClazz) {
        super();
        this.enumClazz = enumClazz;
    }

    @Override
    public EnumMap<K, V> createInstance(final java.lang.reflect.Type type) {
        return new EnumMap<>(enumClazz);
    }
}
