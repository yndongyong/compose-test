/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androiddevchallenge.ui.theme.MyTheme

class MainActivity : AppCompatActivity() {

    private val screenViewModel by viewModels<ScreenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        screenViewModel.screenData.observe(this) {
            setContent {
                MyTheme {
                    MyApp(screenViewModel)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!screenViewModel.onBack()) {
            super.onBackPressed()
        }
    }
}

// Start building your app here!
@Composable
fun MyApp(vm: ScreenViewModel) {
    val curScreen = vm.screenData.observeAsState(true)

    val dogList = arrayListOf<Dog>()
    dogList.add(Dog("bronx1", 1, R.drawable.bronx_1))
    dogList.add(Dog("edison1", 1, R.drawable.edison_1))
    dogList.add(Dog("gunner1", 1, R.drawable.gunner_1))
    dogList.add(Dog("lucas1", 1, R.drawable.lucas_1))

    dogList.add(Dog("bronx2", 1, R.drawable.bronx_1))
    dogList.add(Dog("edison2", 1, R.drawable.edison_1))
    dogList.add(Dog("gunner2", 1, R.drawable.gunner_1))
    dogList.add(Dog("lucas2", 1, R.drawable.lucas_1))

    dogList.add(Dog("bronx3", 1, R.drawable.bronx_1))
    dogList.add(Dog("edison3", 1, R.drawable.edison_1))
    dogList.add(Dog("gunner3", 1, R.drawable.gunner_1))
    dogList.add(Dog("lucas3", 1, R.drawable.lucas_1))


    Crossfade(curScreen) {
        Surface(color = MaterialTheme.colors.background) {

            if (it.value) {
                val state = rememberScrollState()
                LaunchedEffect(Unit) { state.animateScrollTo(0) }
                Column(
                    Modifier
                        .padding(16.dp)
                        .verticalScroll(state)
                ) {
                    Text("Dog list", style = MaterialTheme.typography.h5)

                    dogList.forEach {
                        DogItem(dog = it, onClick = {
                            vm.showDog(it)
                            vm.navigateToDetail()
                        })

                    }
                }
            } else {
                val curDog = vm.curDog.value
                DetailScreen(curDog!!) {
                    vm.cleanDog()
                    vm.navigateToHome()
                }
            }
        }
    }

}


class ScreenViewModel : ViewModel() {


    private val _screen = MutableLiveData<Boolean>(true)

    var screenData: LiveData<Boolean> = _screen

    private var _curDog = MutableLiveData<Dog>(null)

    var curDog: LiveData<Dog> = _curDog

    fun showDog(dog: Dog) {
        _curDog.value = dog
    }

    fun cleanDog() {
        _curDog.value = null
    }

    @MainThread
    fun onBack(): Boolean {
        if (_screen.value != true) {
            _screen.value = false
            return true
        }
        return false
    }

    @MainThread
    fun navigateToDetail() {
        _screen.value = false
    }

    @MainThread
    fun navigateToHome() {
        _screen.value = true
    }
}

@Composable
fun DetailScreen(dog: Dog, onClick: (Dog) -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.padding(16.dp)) {
        AvatarHeaderImage(modifier = Modifier.clickable {
            onClick.invoke(dog)
        }, dog.avatarRes)
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            Toast.makeText(context, "Belong to you!", Toast.LENGTH_LONG).show()
            onClick.invoke(dog)
        }) {
            Text(text = "I want to adoption!")
        }
        Spacer(modifier = Modifier.size(16.dp))
        Button(onClick = {
            onClick.invoke(dog)
        }) {
            Text(text = "I want to see others")
        }
    }
}

@Composable
fun DogItem(dog: Dog, onClick: (Dog) -> Unit) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .clickable {
                onClick.invoke(dog)
//                vm.screenData.value = false
            }
    ) {
        AvatarImage(
            Modifier.padding(end = 8.dp),
            dog.avatarRes
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text("name ${dog.name}", style = MaterialTheme.typography.h5)
            Text(text = "age ${dog.age}")
        }
    }
}

@Composable
fun AvatarImage(modifier: Modifier = Modifier, avatarRes: Int) {
    val image = ImageBitmap.imageResource(avatarRes)
    Image(
        bitmap = image,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier
            .size(120.dp, 80.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
fun AvatarHeaderImage(modifier: Modifier = Modifier, avatarRes: Int) {
    val image = ImageBitmap.imageResource(avatarRes)
    Image(
        bitmap = image,
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier
            .fillMaxWidth(1f)
            .height(200.dp)
            .clip(MaterialTheme.shapes.medium)
    )
}

data class Dog(val name: String, val age: Int, val avatarRes: Int)


@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {

    MyTheme(darkTheme = false) {
        MyApp(ScreenViewModel())
    }
}
