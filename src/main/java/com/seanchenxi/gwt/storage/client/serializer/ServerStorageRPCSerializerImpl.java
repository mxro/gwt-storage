/*
 * Copyright 2013 Xi CHEN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.seanchenxi.gwt.storage.client.serializer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.ClientSerializationStreamReader;
import com.google.gwt.user.client.rpc.impl.Serializer;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Default implementation of {@link StorageSerializer}
 *
 * Use GWT RPC way to realize object's serialization or deserialization.
 *
 */
public final class ServerStorageRPCSerializerImpl implements StorageSerializer {

  private final static Serializer TYPE_SERIALIZER = GWT.create(StorageTypeSerializer.class);
  private final static HashMap<Class<?>, StorageValueType> TYPE_MAP = new HashMap<Class<?>, StorageValueType>();
  static{
    TYPE_MAP.put(boolean[].class, StorageValueType.BOOLEAN_VECTOR);
    TYPE_MAP.put(byte[].class, StorageValueType.BYTE_VECTOR);
    TYPE_MAP.put(char[].class, StorageValueType.CHAR_VECTOR);
    TYPE_MAP.put(double[].class, StorageValueType.DOUBLE_VECTOR);
    TYPE_MAP.put(float[].class, StorageValueType.FLOAT_VECTOR);
    TYPE_MAP.put(int[].class, StorageValueType.INT_VECTOR);
    TYPE_MAP.put(long[].class, StorageValueType.LONG_VECTOR);
    TYPE_MAP.put(short[].class, StorageValueType.SHORT_VECTOR);
    TYPE_MAP.put(String[].class, StorageValueType.STRING_VECTOR);

    TYPE_MAP.put(boolean.class, StorageValueType.BOOLEAN);
    TYPE_MAP.put(byte.class, StorageValueType.BYTE);
    TYPE_MAP.put(char.class, StorageValueType.CHAR);
    TYPE_MAP.put(double.class, StorageValueType.DOUBLE);
    TYPE_MAP.put(float.class, StorageValueType.FLOAT);
    TYPE_MAP.put(int.class, StorageValueType.INT);
    TYPE_MAP.put(long.class, StorageValueType.LONG);
    TYPE_MAP.put(short.class, StorageValueType.SHORT);
    TYPE_MAP.put(String.class, StorageValueType.STRING);
  }

  @Override @SuppressWarnings("unchecked")
  public <T extends Serializable> T deserialize(Class<? super T> clazz, String serializedString) throws SerializationException {
    if (serializedString == null) {
      return null;
    }else if(String.class.equals(clazz)){
      return (T) serializedString;
    }
    ClientSerializationStreamReader reader = new ClientSerializationStreamReader(TYPE_SERIALIZER);
    reader.prepareToRead(serializedString);
    Object obj = findType(clazz).read(reader);
    return obj != null ? (T) obj : null;
  }

  @Override
  public <T extends Serializable> String serialize(Class<? super T> clazz, T instance) throws SerializationException {
    if (instance == null) {
      return null;
    }else if(String.class.equals(clazz)){
      return (String) instance;
    }
    StorageSerializationStreamWriter writer = new StorageSerializationStreamWriter(TYPE_SERIALIZER);
    writer.prepareToWrite();
    if(clazz.isArray()){ // for array type, must write its type name at first
      writer.writeString(TYPE_SERIALIZER.getSerializationSignature(clazz));
    }
    findType(clazz).write(writer, instance);
    return writer.toString();
  }

  private StorageValueType findType(Class<?> clazz) {
    StorageValueType type = TYPE_MAP.get(clazz);
    if (type == null) { // for primitive array, use object writer
      type = clazz.isArray() ? StorageValueType.OBJECT_VECTOR : StorageValueType.OBJECT;
    }
    return type;
  }

}
