/*
 * Copyright 2023 Yu Junyang
 * https://github.com/lowkeyfish
 *
 * This file is part of IDDD.
 *
 * IDDD is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IDDD is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IDDD.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.yujunyang.iddd.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class TripleDESUtils {
    private static final String ALGORITHM = "DESede/CBC/PKCS5Padding";

    public static String encrypt(String data, String key, String iv) {
        try {
            Cipher cipher = cipher(key, iv, Cipher.ENCRYPT_MODE);
            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedData, String key, String iv) {
        try {
            Cipher cipher = cipher(key, iv, Cipher.DECRYPT_MODE);
            byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(192);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] bytes = secretKey.getEncoded();
            String key = Base64.getEncoder().encodeToString(bytes);
            return key;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String iv() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[Cipher.getInstance(ALGORITHM).getBlockSize()];
            secureRandom.nextBytes(iv);
            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            return ivBase64;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Cipher cipher(String key, String iv, int opMode) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            DESedeKeySpec desKeySpec = new DESedeKeySpec(key.getBytes(StandardCharsets.UTF_8));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(opMode, secretKey, ivParameterSpec);
            return cipher;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
