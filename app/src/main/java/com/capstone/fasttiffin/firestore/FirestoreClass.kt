package com.capstone.fasttiffin.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.capstone.fasttiffin.models.CartItem
import com.capstone.fasttiffin.models.Product
import com.capstone.fasttiffin.models.User
import com.capstone.fasttiffin.ui.activities.*
import com.capstone.fasttiffin.ui.fragments.DashboardFragment
import com.capstone.fasttiffin.ui.fragments.ProductsFragment
import com.capstone.fasttiffin.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFirestore = Firebase.firestore

    //Setting data in firebase firestore
    fun registerUser(activity: RegisterActivity, userInfo: User){
        mFirestore.collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }
    }

    //Retrieving data of current user from firebase auth
    fun getCurrentUserID(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser!=null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                // Here we have received the document snapshot which is converted into the User Data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                        Constants.FASTTIFFIN_PREFERENCES,
                        Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                // Key: Value  logged_in_username: Manish Kumar
                editor.putString(
                        Constants.LOGGED_IN_USERNAME,
                        "${user.firstName} ${user.lastName}"
                )
                editor.apply()

                when(activity){
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                        activity.javaClass.simpleName,
                        "Error while getting user details.",
                        e
                )
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>){

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when(activity){
                    is UserProfileActivity ->{

                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the user details.",e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String){
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." +
        Constants.getFileExtension(activity,imageFileURI)
        )

        sRef.putFile(imageFileURI!!).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            Log.e(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )

            // Get the downloadable url from the task snapshot
            taskSnapshot.metadata!!.reference!!.downloadUrl
                .addOnSuccessListener { uri ->
                    Log.e("Downloadable Image URL", uri.toString())
                    when(activity){
                        is UserProfileActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity ->{
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }
            }
        }
            .addOnFailureListener{ exception ->
                // Hide the progress dialog if there is any error and print the error in log.
                when(activity){
                    is UserProfileActivity ->{
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product){
        mFirestore.collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while adding the product details.",
                            e
                    )
                }
    }

    fun getProductList(fragment: Fragment){
        mFirestore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                val productsList: ArrayList<Product> = ArrayList()
                for(i in document.documents){

                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }
                when(fragment){
                    is ProductsFragment ->{
                        fragment.successProductListFromFirestore(productsList)
                    }
            }
        }
    }

    fun getDashboardItemsList(fragment: DashboardFragment){
        mFirestore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for(i in document.documents){

                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productsList.add(product)
                    }
                    fragment.successDashboardItemsList(productsList)
                }
                .addOnFailureListener { e->
                    fragment.hideProgressDialog()
                    Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.",e)
                }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String){
        mFirestore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()
                }
                .addOnFailureListener { e ->
                    fragment.hideProgressDialog()
                    Log.e(fragment.requireActivity().javaClass.simpleName,"Error while deleting the product.",e)
                }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String){
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the product details.",e)
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem){
        mFirestore.collection(Constants.CART_ITEMS)
                .document()
                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {
                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while creating the document for cart item.",e)
                }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String){
        mFirestore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID,getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener { document ->
                    if(document.documents.size > 0){
                        activity.productExistInCart()
                    }
                    else{
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while checking the existing cart list.",e)

                }
    }

    fun getCartList(activity: Activity){
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()
                for(i in document.documents){
                    val cartItem = i.toObject(CartItem::class.java)
                    cartItem!!.id= i.id
                    list.add(cartItem)
                }
                when(activity){
                    is CartListActivity ->{
                        activity.successCartItemsList(list)
                    }
                }

            }
            .addOnFailureListener { e ->
                when(activity){
                    is CartListActivity ->{
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.",e)

            }
    }

    fun getAllProductsList(activity: CartListActivity){
        mFirestore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Products List", document.documents.toString())
                    val productsList: ArrayList<Product> = ArrayList()
                    for(i in document.documents){
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id
                        productsList.add(product)
                    }
                    activity.successProductsListFromFirestore(productsList)
                }
                .addOnFailureListener { e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting all product list.",e)
                }
    }

    fun removeItemFromCart(context: Context, cartId: String){
        mFirestore.collection(Constants.CART_ITEMS)
                .document(cartId)
                .delete()
                .addOnSuccessListener {
                    when(context){
                        is CartListActivity ->{
                            context.itemRemovedSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when(context){
                        is CartListActivity ->{
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(context.javaClass.simpleName,"Error while removing the item from cart list.",e)
                }
    }

    fun updateMyCart(context: Context, cartId: String, itemHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.CART_ITEMS)
                .document(cartId)
                .update(itemHashMap)
                .addOnSuccessListener {
                    when(context){
                        is CartListActivity ->{
                            context.itemUpdateSuccess()
                        }
                    }

                }
                .addOnFailureListener { e->
                    when(context){
                        is CartListActivity ->{
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(context.javaClass.simpleName,"Error while updating the cart item.",e)

                }
    }



}