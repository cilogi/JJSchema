// Copyright (c) 2016 Cilogi. All Rights Reserved.
//
// File:        Util.java  (9/15/16)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Cilogi (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//


package com.github.reinert.jjschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.reinert.jjschema.exception.TypeException;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;

public class Util {
    @SuppressWarnings("unused")
    static final Logger LOG = LoggerFactory.getLogger(Util.class);

    private Util() {}

    static boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @SuppressWarnings({"unused"})
    public static void saveClass(JsonSchemaGenerator generator, Class clazz, File rootDir) throws TypeException, IOException {
        JsonNode productSchema = generator.generateSchema(clazz);
        String s = JacksonUtils.prettyPrint(productSchema);
        File file = new File(rootDir, nameOf(clazz) + ".json");
        storeBytes(s.getBytes(Charsets.UTF_8), file);
    }

    private static void storeBytes(byte[] bytes, File file) throws IOException {
        FileChannel dstChannel = new FileOutputStream(file).getChannel();
        try {
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            while (buf.hasRemaining()) {
                dstChannel.write(buf);
            }
        }  finally {
            dstChannel.close();
        }
    }

    private static String nameOf(Class clazz) {
        SchemaFileName ann = (SchemaFileName)clazz.getAnnotation(SchemaFileName.class);
        if (ann != null) {
            return ann.value();
        } else {
            String full = clazz.getName();
            String[] sub = full.split("\\.");
            return sub[sub.length-1];
        }
    }

    public static String refSchemaForClass(Class clazz) {
        SchemaFileName ann = (SchemaFileName)clazz.getAnnotation(SchemaFileName.class);
        return (ann != null) ? ann.value() : null;
    }
}
