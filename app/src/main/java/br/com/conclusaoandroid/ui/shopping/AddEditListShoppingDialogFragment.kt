package br.com.conclusaoandroid

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import br.com.conclusaoandroid.common.Utils
import br.com.conclusaoandroid.databinding.DialogfragmentAddEditListShoppingBinding
import br.com.conclusaoandroid.domain.model.Product
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.samuelribeiro.mycomponents.CustomToast

class AddEditListShoppingDialogFragment : DialogFragment() {

    private lateinit var binding: DialogfragmentAddEditListShoppingBinding
    private lateinit var productName: String
    private lateinit var productValue: String
    private lateinit var productCurrent: Product
    private var productAmount: Int? = 0
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
        setupButtonPositive()
        setupButtonNegative()
    }

    private fun setupButtonNegative() {
        binding.dialogTest.setOnClickButtonNegative { dismiss() }
    }

    private fun setupButtonPositive() {
        binding.dialogTest.setOnClickButtonPositive {
            dismiss()
            editProduct(
                binding.dialogTest.getTextFirstField(),
                binding.dialogTest.getTextSecondField().toDouble(),
                binding.dialogTest.getTextThirdField().toInt()
            )
            CustomToast.success(requireActivity(), getString(R.string.successfully_edited))
        }
    }

    private fun setTitle() {
        binding.dialogTest.setTitleDialog(getString(R.string.edit))
    }

    private fun fillInFields() {
        binding.dialogTest.setTextFirstField(productName)
        binding.dialogTest.setTextSecondField(productValue)
        binding.dialogTest.setTextThirdField(productAmount.toString())
    }

    private fun editProduct(description: String, value: Double, amount: Int) {

        val purchaseValue = Utils.calcPurchaseValue(amount, value)

        Firebase.firestore
            .collection("shopping")
            .document(documentId)
            .collection("products")
            .document(productCurrent.documentId.toString())
            .update(
                "description", description,
                "purchaseValue", purchaseValue,
                "value", value,
                "amount", amount,
            )
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    fun receiveData(
        productName: String?,
        productValue: String,
        productAmount: Int?,
        productCurrent: Product
    ) {
        this.productName = productName.toString()
        this.productValue = productValue
        this.productAmount = productAmount
        this.productCurrent = productCurrent
    }
}
