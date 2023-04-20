package br.com.conclusaoandroid

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.DialogFragment
import br.com.conclusaoandroid.databinding.DialogfragmentAddEditListShoppingBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.samuelribeiro.mycomponents.CustomToast

class AddEditListShoppingDialogFragment : DialogFragment() {

    private lateinit var binding: DialogfragmentAddEditListShoppingBinding
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

        binding.dialogTest.setTitleDialog("Olá SAMUEL $documentId")
        binding.dialogTest.setOnClickButtonPositive {
            CustomToast.success(requireActivity(), "Ação Dialog")
        }
        binding.dialogTest.setOnClickButtonNegative { dismiss() }
    }

}