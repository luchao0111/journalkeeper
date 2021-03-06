/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.journalkeeper.rpc.remoting.serialize;

import io.journalkeeper.rpc.remoting.transport.codec.Decoder;
import io.journalkeeper.rpc.remoting.transport.codec.Encoder;
import io.netty.buffer.ByteBuf;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LiYue
 * Date: 2019-03-28
 */
public class CodecSupport {
    public static String decodeString(ByteBuf byteBuf) {
        return new String(decodeBytes(byteBuf), StandardCharsets.UTF_8);
    }

    public static void encodeString(ByteBuf byteBuf, String str) {
        byte[] bytes = str == null ? new byte[0] : str.getBytes(StandardCharsets.UTF_8);
        encodeBytes(byteBuf, bytes);
    }

    public static byte[] decodeBytes(ByteBuf byteBuf) {
        int length = byteBuf.readInt();
        if (length > 0) {
            byte[] buff = new byte[length];
            byteBuf.readBytes(buff);
            return buff;
        }
        return new byte[0];
    }

    public static void encodeBytes(ByteBuf byteBuf, byte[] bytes) {
        if (null == bytes) {
            bytes = new byte[0];
        }
        byteBuf.writeInt(bytes.length);
        if (bytes.length > 0) {
            byteBuf.writeBytes(bytes);
        }
    }


    public static void encodeLong(ByteBuf byteBuf, long l) {
        byteBuf.writeLong(l);
    }

    public static long decodeLong(ByteBuf byteBuf) {
        return byteBuf.readLong();
    }

    public static void encodeInt(ByteBuf byteBuf, int i) {
        byteBuf.writeInt(i);
    }

    public static int decodeInt(ByteBuf byteBuf) {
        return byteBuf.readInt();
    }

    public static void encodeShort(ByteBuf byteBuf, short s) {
        byteBuf.writeShort(s);
    }

    public static short decodeShort(ByteBuf byteBuf) {
        return byteBuf.readShort();
    }

    public static void encodeByte(ByteBuf byteBuf, byte b) {
        byteBuf.writeByte(b);
    }

    public static byte decodeByte(ByteBuf byteBuf) {
        return byteBuf.readByte();
    }

    public static <T> void encodeList(ByteBuf byteBuf, List<T> list, Encoder itemEncoder) {
        encodeCollection(byteBuf, list, itemEncoder);
    }

    public static <T> void encodeCollection(ByteBuf byteBuf, Collection<T> collection, Encoder itemEncoder) {
        if (null == collection) {
            byteBuf.writeInt(-1);
        } else {
            byteBuf.writeInt(collection.size());
            collection.forEach(item -> itemEncoder.encode(item, byteBuf));
        }
    }


    public static <K, V> void encodeMap(ByteBuf byteBuf, Map<K, V> map, Encoder keyEncoder, Encoder valueEncoder) {
        if (null == map) {
            byteBuf.writeInt(-1);
        } else {
            byteBuf.writeInt(map.size());
            map.forEach((key, value) -> {
                keyEncoder.encode(key, byteBuf);
                valueEncoder.encode(value, byteBuf);
            });
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> decodeList(ByteBuf byteBuf, Decoder itemDecoder) {
        int size = byteBuf.readInt();
        if (size < 0) {
            return null;
        } else {
            List<T> list = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                list.add((T) itemDecoder.decode(byteBuf));
            }
            return list;
        }
    }

    public static <T> Collection<T> decodeCollection(ByteBuf byteBuf, Decoder itemDecoder) {
        return decodeList(byteBuf, itemDecoder);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> decodeMap(ByteBuf byteBuf, Decoder keyDecoder, Decoder valueDecoder) {
        int size = byteBuf.readInt();
        if (size < 0) {
            return null;
        } else {
            Map<K, V> map = new HashMap<>(size);
            for (int i = 0; i < size; i++) {
                map.put((K) keyDecoder.decode(byteBuf), (V) valueDecoder.decode(byteBuf));
            }
            return map;
        }
    }

    public static void encodeUri(ByteBuf byteBuf, URI uri) {
        encodeString(byteBuf, null == uri ? null : uri.toString());
    }

    public static URI decodeUri(ByteBuf byteBuf) {
        String uriString = decodeString(byteBuf);
        if (uriString.isEmpty()) return null;
        else return URI.create(uriString);
    }

    public static void encodeBoolean(ByteBuf byteBuf, boolean bool) {
        byteBuf.writeByte(bool ? 0X1 : 0X0);
    }

    public static boolean decodeBoolean(ByteBuf byteBuf) {
        return byteBuf.readByte() == 0X1;
    }

    public static void encodeUUID(ByteBuf byteBuf, UUID uuid) {
        long mostSigBits = 0L;
        long leastSigBits = 0L;

        if (null != uuid) {
            mostSigBits = uuid.getMostSignificantBits();
            leastSigBits = uuid.getLeastSignificantBits();
        }
        encodeLong(byteBuf, mostSigBits);
        encodeLong(byteBuf, leastSigBits);
    }

    public static UUID decodeUUID(ByteBuf byteBuf) {
        long mostSigBits = decodeLong(byteBuf);
        long leastSigBits = decodeLong(byteBuf);

        if (mostSigBits == 0L && leastSigBits == 0L) {
            return null;
        } else {
            return new UUID(mostSigBits, leastSigBits);
        }
    }
}
