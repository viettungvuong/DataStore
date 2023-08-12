package com.tung.datastore

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
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

val Context.dataStorePrefs: DataStore<Preferences> by preferencesDataStore(name = "settings")
val nameKey=stringPreferencesKey("name") //key chứa tên

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = this.dataStorePrefs

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

@Composable
fun Composable(context: Context) {
    var name  by remember { mutableStateOf("") }
    //truyền tên sẽ hiện trong TextView
    var inputText by remember { mutableStateOf(name) }

    LaunchedEffect(true){
        name = context.dataStorePrefs.data.first()[nameKey]?:""
    }


    MaterialTheme{
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Hello $name")

            //giá trị textField sẽ được chứa trong input text
            TextField(value = inputText, onValueChange = {inputText = it}, label = { Text("Name") })

            Box(modifier = Modifier.padding(vertical = 50.dp)) {
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
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Composable(LocalContext.current)
}