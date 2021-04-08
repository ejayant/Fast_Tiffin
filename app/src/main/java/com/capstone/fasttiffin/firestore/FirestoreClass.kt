package com.capstone.fasttiffin.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.capstone.fasttiffin.models.*
import com.capstone.fasttiffin.ui.activities.*
import com.capstone.fasttiffin.ui.fragments.DashboardFragment
import com.capstone.fasttiffin.ui.fragments.OrdersFragment
import com.capstone.fasttiffin.ui.fragments.ProductsFragment
import com.capstone.fasttiffin.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirestoreClass {

    private val mFireStore = Firebase.firestore

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
                .document(userInfo.id)
                .set(userInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.userRegistrationSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while registering the user", e)
                }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser

        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


    fun getUserDetails(activity: Activity) {

        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.i(activity.javaClass.simpleName, document.toString())

                    val user = document.toObject(User::class.java)!!

                    val sharedPreferences = activity.getSharedPreferences(
                            Constants.FASTTIFFIN_PREFERENCES, Context.MODE_PRIVATE
                    )

                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.putString(
                            Constants.LOGGED_IN_USERNAME,
                            "${user.firstName} ${user.lastName}"
                    )

                    editor.apply()

                    when (activity) {
                        is LoginActivity -> {
                            activity.userLoggedInSuccess(user)
                        }
                        is SettingsActivity -> {
                            activity.userDetailsSuccess(user)
                        }
                    }
                }
                .addOnFailureListener {
                    when (activity) {
                        is LoginActivity -> {
                            activity.hideProgressDialog()
                        }
                        is SettingsActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName, "Error while updating user details ", it)
                }

    }


    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS).document(getCurrentUserID())
                .update(userHashMap)
                .addOnSuccessListener {
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.userProfileUpdateSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName, "Error while updating user details ", e)
                }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURi: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                imageType + System.currentTimeMillis() + "." + Constants.getFileExtension(
                        activity, imageFileURi
                )
        )
        sRef.putFile(imageFileURi!!).addOnSuccessListener { taskSnapshot ->
            Log.e("Firebase Image Uri", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())

            taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }

                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }


                        Log.e("Download Image URL", uri.toString())
                    }
        }

                .addOnFailureListener { exception ->
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.hideProgressDialog()
                        }
                        is AddProductActivity -> {
                            activity.hideProgressDialog()
                        }
                    }
                    Log.e(activity.javaClass.simpleName, exception.message, exception)
                }

    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
                .document()
                .set(productInfo, SetOptions.merge())
                .addOnSuccessListener {
                    activity.productUploadSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error in uploading Product details", e)
                }
    }

    fun getProductList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e("Products List", document.documents.toString())

                    val productList: ArrayList<Product> = ArrayList()

                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productList.add(product)
                    }
                    when (fragment) {
                        is ProductsFragment -> {
                            fragment.successProductListFromFirestore(productList)
                        }
                    }
                }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())

                    val productList: ArrayList<Product> = ArrayList()

                    for (i in document.documents) {
                        val product = i.toObject(Product::class.java)!!
                        product.product_id = i.id

                        productList.add(product)
                    }
                    when (fragment) {
                        else -> {
                            fragment.successDashboardItemsList(productList)
                        }
                    }
                }
                .addOnFailureListener {
                    fragment.hideProgressDialog()
                }
    }

    fun deleteProduct(fragment:ProductsFragment,productId: String){
        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .delete()
                .addOnSuccessListener {
                    fragment.productDeleteSuccess()
                }
                .addOnFailureListener { e->
                    fragment.hideProgressDialog()
                    Log.e(fragment.requireActivity().javaClass.simpleName,"Error while deleting the product.",e)
                }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {


        mFireStore.collection(Constants.PRODUCTS)
                .document(productId)
                .get()
                .addOnSuccessListener { document ->


                    Log.e(activity.javaClass.simpleName, document.toString())


                    val product = document.toObject(Product::class.java)!!

                    activity.productDetailsSuccess(product)
                }
                .addOnFailureListener { e ->


                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while getting the product details.", e)
                }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem) {

        mFireStore.collection(Constants.CART_ITEMS)
                .document()

                .set(addToCart, SetOptions.merge())
                .addOnSuccessListener {


                    activity.addToCartSuccess()
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while creating the document for cart item.",
                            e
                    )
                }
    }


    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {

        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .whereEqualTo(Constants.PRODUCT_ID, productId)
                .get()
                .addOnSuccessListener { document ->

                    Log.e(activity.javaClass.simpleName, document.documents.toString())


                    if (document.documents.size > 0) {
                        activity.productExistInCart()
                    } else {
                        activity.hideProgressDialog()
                    }
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while checking the existing cart list.",
                            e
                    )
                }
    }


    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEMS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->


                    Log.e(activity.javaClass.simpleName, document.documents.toString())


                    val list: ArrayList<CartItem> = ArrayList()


                    for (i in document.documents) {

                        val cartItem = i.toObject(CartItem::class.java)!!
                        cartItem.id = i.id

                        list.add(cartItem)
                    }

                    when (activity) {
                        is CartListActivity -> {
                            activity.successCartItemsList(list)
                        }
                        is CheckoutActivity -> {
                            activity.successCartItemsList(list)
                        }
                    }
                }
                .addOnFailureListener { e ->

                    when (activity) {
                        is CartListActivity -> {
                            activity.hideProgressDialog()
                        }
                        is CheckoutActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e(activity.javaClass.simpleName, "Error while getting the cart list items.", e)
                }
    }


    fun removeItemFromCart(context: Context, cart_id: String) {

        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id)
                .delete()
                .addOnSuccessListener {


                    when (context) {
                        is CartListActivity -> {
                            context.itemRemovedSuccess()
                        }
                    }
                }
                .addOnFailureListener { e ->


                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }
                    Log.e(
                            context.javaClass.simpleName,
                            "Error while removing the item from the cart list.",
                            e
                    )
                }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {


        mFireStore.collection(Constants.CART_ITEMS)
                .document(cart_id)
                .update(itemHashMap)
                .addOnSuccessListener {


                    when (context) {
                        is CartListActivity -> {
                            context.itemUpdateSuccess()
                        }
                    }

                }
                .addOnFailureListener { e ->


                    when (context) {
                        is CartListActivity -> {
                            context.hideProgressDialog()
                        }
                    }

                    Log.e(
                            context.javaClass.simpleName,
                            "Error while updating the cart item.",
                            e
                    )
                }
    }

    fun getAllProductsList(activity: Activity) {

        mFireStore.collection(Constants.PRODUCTS)
                .get()
                .addOnSuccessListener { document ->


                    Log.e("Products List", document.documents.toString())


                    val productsList: ArrayList<Product> = ArrayList()


                    for (i in document.documents) {

                        val product = i.toObject(Product::class.java)
                        product!!.product_id = i.id

                        productsList.add(product)
                    }
                    when (activity){
                        is CartListActivity -> {
                            activity.successProductsListFromFireStore(productsList)
                        }
                        is CheckoutActivity -> {
                            activity.successProductsListFromFirestore(productsList)
                        }
                    }

                }
                .addOnFailureListener { e ->

                    when (activity) {
                        is CartListActivity -> {
                            activity.hideProgressDialog()
                        }
                        is CheckoutActivity -> {
                            activity.hideProgressDialog()
                        }
                    }

                    Log.e("Get Product List", "Error while getting all product list.", e)
                }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {


        mFireStore.collection(Constants.ADDRESSES)
                .document()

                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {

                    activity.addUpdateAddressSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while adding the address.",
                            e
                    )
                }
    }

    fun getAddressesList(activity: AddressListActivity) {

        mFireStore.collection(Constants.ADDRESSES)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->

                    Log.e(activity.javaClass.simpleName, document.documents.toString())

                    val addressList: ArrayList<Address> = ArrayList()


                    for (i in document.documents) {

                        val address = i.toObject(Address::class.java)!!
                        address.id = i.id

                        addressList.add(address)
                    }

                    activity.successAddressListFromFirestore(addressList)
                }
                .addOnFailureListener { e ->

                    activity.hideProgressDialog()

                    Log.e(activity.javaClass.simpleName, "Error while getting the address list.", e)
                }
    }


    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
                .document(addressId)

                .set(addressInfo, SetOptions.merge())
                .addOnSuccessListener {


                    activity.addUpdateAddressSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while updating the Address.",
                            e
                    )
                }
    }



    fun deleteAddress(activity: AddressListActivity, addressId: String) {

        mFireStore.collection(Constants.ADDRESSES)
                .document(addressId)
                .delete()
                .addOnSuccessListener {


                    activity.deleteAddressSuccess()
                }
                .addOnFailureListener { e ->
                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while deleting the address.",
                            e
                    )
                }
    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {

        mFireStore.collection(Constants.ORDERS)
                .document()

                .set(order, SetOptions.merge())
                .addOnSuccessListener {


                    activity.orderPlacedSuccess()

                }
                .addOnFailureListener { e ->


                    activity.hideProgressDialog()
                    Log.e(
                            activity.javaClass.simpleName,
                            "Error while placing an order.",
                            e
                    )
                }
    }

    fun updateAllDetails(activity: CheckoutActivity,cartList:ArrayList<CartItem>){
        val writeBatch = mFireStore.batch()
        for (cartItem in cartList){
            val productHashMap = HashMap<String,Any>()

            productHashMap[Constants.STOCK_QUANTITY] =
                    (cartItem.stock_quantity.toInt() - cartItem.cart_quantity.toInt()).toString()

            val documentReference = mFireStore.collection(Constants.PRODUCTS)
                    .document(cartItem.product_id)

            writeBatch.update(documentReference, productHashMap)
        }
        for(cartItem in cartList){
            val documentReference = mFireStore.collection(Constants.CART_ITEMS)
                    .document(cartItem.id)
            writeBatch.delete(documentReference)
        }
        writeBatch.commit().addOnSuccessListener {
            activity.allDetailsUpdatedSuccessfully()
        }.addOnFailureListener { e ->
            activity.hideProgressDialog()
            Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing an order.",
                    e
            ) }
    }

    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.ORDERS)
                .whereEqualTo(Constants.USER_ID, getCurrentUserID())
                .get()
                .addOnSuccessListener { document ->
                    Log.e(fragment.javaClass.simpleName, document.documents.toString())
                    val list: ArrayList<Order> = ArrayList()

                    for (i in document.documents) {

                        val orderItem = i.toObject(Order::class.java)!!
                        orderItem.id = i.id

                        list.add(orderItem)
                    }

                    fragment.populateOrdersListInUI(list)
                }
                .addOnFailureListener { e ->
                    // Here call a function of base activity for transferring the result to it.

                    fragment.hideProgressDialog()

                    Log.e(fragment.javaClass.simpleName, "Error while getting the orders list.", e)
                }
    }



}