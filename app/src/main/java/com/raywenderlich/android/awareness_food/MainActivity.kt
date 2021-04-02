/*
 * Copyright (c) 2021 Razeware LLC
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 * 
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.awareness_food

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.google.android.material.snackbar.Snackbar
import com.raywenderlich.android.awareness_food.data.Recipe
import com.raywenderlich.android.awareness_food.databinding.ActivityMainBinding
import com.raywenderlich.android.awareness_food.monitor.NetworkMonitor
import com.raywenderlich.android.awareness_food.monitor.NetworkState
import com.raywenderlich.android.awareness_food.repositories.models.RecipeApiState
import com.raywenderlich.android.awareness_food.viewmodels.MainViewModel
import com.raywenderlich.android.awareness_food.viewmodels.UiLoadingState
import com.raywenderlich.android.awareness_food.views.IngredientView
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  @Inject
  lateinit var networkMonitor: NetworkMonitor

  private val viewModel: MainViewModel by viewModels { viewModelFactory }
  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)
    setTheme(R.style.AppTheme)
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    lifecycle.addObserver(networkMonitor)

    viewModel.loadingState.observe(this, Observer { uiLoadingState ->
      binding.progressBar.isVisible = when (uiLoadingState) {
        UiLoadingState.Loading -> true
        UiLoadingState.NotLoading -> false
      }
    })

    viewModel.recipeState.observe(this, Observer {
      when (it) {
        RecipeApiState.Error -> showNetworkUnavailableAlert(R.string.error)
        is RecipeApiState.Result -> buildViews(it.recipe)
      }

    })
    viewModel.getRandomRecipe()

    networkMonitor.networkAvailableStateFlow.asLiveData().observe(this, Observer { networkState ->
      if (networkState is NetworkState.Unavailable) {
        showNetworkUnavailableAlert(R.string.network_is_unavailable)
      }
    })
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.menu_refresh -> {
      clearViews()
      viewModel.getRandomRecipe()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.main_menu, menu)
    return true
  }

  private fun buildViews(recipe: Recipe) {
    with(binding) {
      recipeInstructionsTitle.text = getString(R.string.instructions)
      recipeIngredientsTitle.text = getString(R.string.ingredients)
      recipeName.text = recipe.title
      recipeSummary.text = HtmlCompat.fromHtml(recipe.summary, 0)
      recipeInstructions.text = HtmlCompat.fromHtml(recipe.instructions, 0)
      Picasso.with(this@MainActivity).load(recipe.image).into(recipeImage)
      recipe.ingredients.forEachIndexed { index, ingredient ->
        val ingredientView = IngredientView(this@MainActivity, ingredient, index != 0)
        recipeIngredients.addView(ingredientView)
      }
    }
  }

  private fun clearViews() {
    with(binding) {
      recipeName.text = ""
      recipeSummary.text = ""
      recipeInstructions.text = ""
      recipeImage.setImageDrawable(null)
      recipeIngredientsTitle.text = ""
      recipeIngredients.removeAllViews()
      recipeInstructionsTitle.text = ""
    }
  }

  private fun showNetworkUnavailableAlert(message: Int) {
    Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.retry) {
          viewModel.getRandomRecipe()
        }.apply {
          view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
          show()
        }
  }
}
