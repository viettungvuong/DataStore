package com.tung.datastore

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.tung.datastore.ui.theme.DataStoreTheme
import java.io.InputStream
import java.io.OutputStream


class ItemList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ViewList()
        }
    }
}

@Composable
fun ViewList() {
    var items = ArrayList<String>()
    MaterialTheme{
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            LazyColumn {
                items(items.size) { i ->
                    ItemRow(items[i])
                }
            }
        }
    }
}

@Composable
fun ItemRow(item: String) {
    // Composable representing the layout of a single item
    // Customize this based on your item's layout requirements
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    ViewList()
}