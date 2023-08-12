package com.tung.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.Flow

object StringListSerializer: Serializer<StringList>{
    override val defaultValue: StringList
        get() = StringList.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): StringList {
        try{
            return StringList.parseFrom(input)
        }
        catch (exception: InvalidProtocolBufferException){
            throw CorruptionException("Cannot read proto",exception)
        }
    }

    override suspend fun writeTo(t: StringList, output: OutputStream)=t.writeTo(output)

}


