package br.com.conclusaoandroid

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import br.com.conclusaoandroid.databinding.DialogCreatedBinding
import com.samuelribeiro.mycomponents.CustomToast

class CustomDialogFragment : DialogFragment() {

    private lateinit var binding: DialogCreatedBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCreatedBinding.inflate(inflater)
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

        binding.dialogTest.setTitleDialog("Olá SAMUEL")
        binding.dialogTest.setOnClickButtonPositive {
            CustomToast.success(requireActivity(), "Ação Dialog")
        }
        binding.dialogTest.setOnClickButtonNegative {
            CustomToast.error(requireActivity(), "Fechar Dialog")
        }
    }

}