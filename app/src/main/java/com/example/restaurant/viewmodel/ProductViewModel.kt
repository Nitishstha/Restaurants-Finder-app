package com.example.restaurant.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.restaurant.Model.ProductModel
import com.example.restaurant.repo.ProductRepo

class ProductViewModel(val repo: ProductRepo) : ViewModel() {

    fun addProduct(product: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.addProduct(product, callback)
    }

    fun updateProduct(model: ProductModel, callback: (Boolean, String) -> Unit) {
        repo.updateProduct(model, callback)
    }

    fun deleteProduct(productId: String, callback: (Boolean, String) -> Unit) {
        repo.deleteProduct(productId, callback)
    }

    private val _products = MutableLiveData<ProductModel?>()
    val products: MutableLiveData<ProductModel?> get() = _products

    private val _allProducts = MutableLiveData<List<ProductModel>?>()
    val allProducts: MutableLiveData<List<ProductModel>?> get() = _allProducts

    private val _loading = MutableLiveData<Boolean>()
    val loading: MutableLiveData<Boolean> get() = _loading

    fun getAllProduct() {
        _loading.postValue(true)
        repo.getAllProduct { success, message, data ->
            if (success) {
                _loading.postValue(false)
                _allProducts.postValue(data)
            }
        }
    }

    fun getProductById(productId: String) {
        repo.getProductById(productId) { success, message, data ->
            if (success) {
                _products.postValue(data)
            }
        }
    }

    fun getProductByCategory(
        categoryId: String,
        callback: (Boolean, String, List<ProductModel>?) -> Unit
    ) {
        repo.getProductByCategory(categoryId, callback)
    }

    fun uploadImage(
        context: Context, imageUri: Uri, callback: (String?) -> Unit
    ) {
        repo.uploadImage(context, imageUri, callback)
    }
}
