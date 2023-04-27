package br.com.conclusaoandroid

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.DialogfragmentAddEditListShoppingBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.samuelribeiro.mycomponents.CustomToast

class AddEditListShoppingDialogFragment : DialogFragment() {

    private lateinit var binding: DialogfragmentAddEditListShoppingBinding
    private lateinit var productName: String
    private lateinit var productValue: String
    private var productAmount: Int = 0
    private val productCurrent by lazy {
        binding.dialogTest.getTextFirstField()
    }
    private val valueCurrent by lazy {
        binding.dialogTest.getTextSecondField()
    }
    private val documentId by lazy {
        val bundle = requireActivity().intent.extras
        bundle?.getString("documentId").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogfragmentAddEditListShoppingBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle()
        fillInFields()
        setupButtonPositive(valueCurrent, productCurrent)
        setupButtonNegative()
    }

    private fun setupButtonNegative() {
        binding.dialogTest.setOnClickButtonNegative { dismiss() }
    }

    private fun setupButtonPositive(valueCurrent: String, productCurrent: String) {
        binding.dialogTest.setOnClickButtonPositive {
            dismiss()
            addProduct(valueCurrent.toDouble(), productCurrent, productAmount)
            CustomToast.success(requireActivity(), getString(R.string.registered_successfully))
        }
    }

    private fun setTitle() {
        binding.dialogTest.setTitleDialog(getString(R.string.add_product))
    }

    private fun fillInFields() {
        binding.dialogTest.setTextFirstField(productName)
        binding.dialogTest.setTextSecondField(productValue)
    }

    private fun addProduct(value: Double, description: String, amount: Int) {
        val purchaseValue = Utils.calcPurchaseValue(amount, value)

        val product = hashMapOf(
            "description" to description,
            "value" to value,
            "amount" to amount,
            "purchaseValue" to purchaseValue
        )

        Firebase.firestore
            .collection("shopping")
            .document(documentId)
            .collection("products")
            .add(product)
            .addOnSuccessListener {}
            .addOnFailureListener { e ->
                CustomToast.error(requireActivity(), "Error")
            }
    }

    fun receiveData(productName: String, productValue: String, productAmount: Int) {
        this.productName = productName
        this.productValue = productValue
        this.productAmount = productAmount
    }

}