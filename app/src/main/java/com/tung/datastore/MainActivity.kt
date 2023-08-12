package com.tung.datastore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tung.datastore.ui.theme.DataStoreTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull

val Context.stringListDataStore: DataStore<StringList> by dataStore(
    fileName = "StringList.proto",
    serializer = StringListSerializer
)
val Context.dataStorePrefs: DataStore<Preferences> by preferencesDataStore(name = "settings")
val nameKey=stringPreferencesKey("name") //key chứa tên

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Composable(this)
        }
    }
}

//lưu name vào prefs với hàm async
suspend fun saveNameToPrefs(newName: String, context: Context){
    context.dataStorePrefs.edit{
        preferences ->
        preferences[nameKey]=newName
    }
}

//lấy list
suspend fun DataStore<StringList>.getStringList(): List<String> {
    return try {
        val stringLists = this.data.first() //lấy single value (giá trị đầu tiên)
        val nameList = stringLists.nameList
        if (nameList != null) {
            nameList
        } else {
            emptyList()
        }
    } catch (e: Exception) {
        emptyList()
    }
}

//cập nhật list
suspend fun DataStore<StringList>.update(string: String) {
    this.updateData { currentStringList ->
        currentStringList.toBuilder().addName(string).build()
    }
}

@Composable
fun Composable(context: Context) {
    var name  by remember { mutableStateOf("") }
    //truyền tên sẽ hiện trong TextView

    var inputText by remember { mutableStateOf(name) }

    //list
    var listState = remember { mutableStateListOf<String>() }

    LaunchedEffect(true){
        name = context.dataStorePrefs.data.first()[nameKey]?:""
        listState.addAll(context.stringListDataStore.getStringList())
    }


    MaterialTheme{
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Hello $name")

            //giá trị textField sẽ được chứa trong input text
            TextField(value = inputText, onValueChange = {inputText = it}, label = { Text("Name") })

            Box(modifier = Modifier.padding(vertical = 50.dp)) {
                Row {
                    Button(onClick = {
                        name = inputText

                        //chạy trong coroutine kotlin
                        runBlocking {
                            launch {
                                saveNameToPrefs(inputText, context)
                            }
                        }

                        //hiện toast thông báo
                        Toast.makeText(context, "Saved $inputText successfully", Toast.LENGTH_SHORT)
                            .show()

                    }) {
                        Text("Change name")
                    }

                    Button(onClick = {
                        listState+=inputText //thêm phần tử mới



                        runBlocking {
                            launch {
                                context.stringListDataStore.update(inputText)
                            }
                        }
                        //cập nhật vào dataStore

                        //hiện toast thông báo
                        Toast.makeText(context, "Added $inputText successfully", Toast.LENGTH_SHORT)
                            .show()

                        inputText=""

                    }) {
                        Text("Add name")
                    }
                }

            }

            LazyColumn{
                itemsIndexed(listState){
                        index, string -> Text("${index+1}. $string")
                }
            }
        }


    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Composable(LocalContext.current)
}