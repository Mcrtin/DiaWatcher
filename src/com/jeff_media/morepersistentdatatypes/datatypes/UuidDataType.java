/*
 * Copyright (c) 2022 Alexander Majka (mfnalex) / JEFF Media GbR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * If you need help or have any suggestions, feel free to join my Discord and head to #programming-help:
 *
 * Discord: https://discord.jeff-media.com/
 *
 * If you find this library helpful or if you're using it one of your paid plugins, please consider leaving a donation
 * to support the further development of this project :)
 *
 * Donations: https://paypal.me/mfnalex
 */

package com.jeff_media.morepersistentdatatypes.datatypes;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UuidDataType implements PersistentDataType<byte[], OfflinePlayer> {
    public static final PersistentDataType<byte[], OfflinePlayer> OFFLINE_PLAYER = new UuidDataType();
    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<OfflinePlayer> getComplexType() {
        return OfflinePlayer.class;
    }

    @Override
    public byte[] toPrimitive(final OfflinePlayer complex, @NotNull final PersistentDataAdapterContext context) {
        final UUID uuid = complex.getUniqueId();
        final ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public @NotNull OfflinePlayer fromPrimitive(final byte [] primitive, @NotNull final PersistentDataAdapterContext context) {
        final ByteBuffer bb = ByteBuffer.wrap(primitive);
        final long firstLong = bb.getLong();
        final long secondLong = bb.getLong();
        return Bukkit.getOfflinePlayer(new UUID(firstLong, secondLong));
    }
}
