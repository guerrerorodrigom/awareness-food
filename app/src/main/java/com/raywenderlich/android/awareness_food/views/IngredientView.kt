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

package com.raywenderlich.android.awareness_food.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textview.MaterialTextView
import com.raywenderlich.android.awareness_food.R
import com.raywenderlich.android.awareness_food.data.Ingredient
import com.squareup.picasso.Picasso


class IngredientView @JvmOverloads constructor(
    context: Context,
    private val ingredient: Ingredient,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {
  init {
    init()
  }

  private fun init() {
    View.inflate(context, R.layout.ingredient_item, this)
    val ingredientName = findViewById<MaterialTextView>(R.id.ingredient_name)
    val ingredientImage = findViewById<AppCompatImageView>(R.id.ingredient_image)

    ingredientName.text = context.getString(R.string.ingredient, ingredient.amount, ingredient.unit, ingredient.name)
    Picasso.with(context).load(ingredient.imageUrl).into(ingredientImage)
  }

  private fun addDivider() {
    val viewDivider = View(context)
    val dividerHeight = (resources.displayMetrics.density * 1).toInt() // 1dp to pixels

    viewDivider.layoutParams = RelativeLayLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dividerHeight)
    viewDivider.setBackgroundColor(context(R.color.colorDivider));
  }
}