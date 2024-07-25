package com.abdullahalhakimi.fedeinanimation

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.abdullahalhakimi.fedeinanimation.ui.theme.FadeInAnimation
import com.abdullahalhakimi.fedeinanimation.ui.util.provideTimeAsState
import com.abdullahalhakimi.fedeinanimation.ui.component.disappear_shader
import kotlin.random.Random

class MainActivity() : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var motion by remember { mutableFloatStateOf(0f) }
            FadeInAnimation {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    AnimationComponent(
                        modifier = Modifier
                            .size(250.dp)
                            .clickable {
                                motion = if (motion == 1f) 0f else 1f
                            },
                        animStage = motion,
                        content = {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = R.drawable.compose_logo),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AnimationComponent(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    animStage: Float = 0f,
) {
    val shader by remember {
        mutableStateOf(RuntimeShader(disappear_shader))
    }
    val time by provideTimeAsState(Random.nextFloat() * 10f)

    val anim = animateFloatAsState(
        targetValue = animStage,
        tween(durationMillis = 2000, easing = FastOutSlowInEasing),
        label = ""
    )

    Box(modifier = modifier
        .onSizeChanged { size ->
            shader.setFloatUniform(
                "resolution",
                size.width.toFloat(),
                size.height.toFloat()
            )
        }
        .graphicsLayer {
            shader.setFloatUniform("time", time)
            shader.setFloatUniform("percentage", anim.value)
            this.renderEffect = RenderEffect
                .createRuntimeShaderEffect(shader, "image")
                .asComposeRenderEffect()
        }) {
        content()
    }
}