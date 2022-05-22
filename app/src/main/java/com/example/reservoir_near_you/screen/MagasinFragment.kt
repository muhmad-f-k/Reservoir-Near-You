package com.example.reservoir_near_you.screen

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMagasinBinding
import com.example.reservoir_near_you.model.Magasin
import com.example.reservoir_near_you.repository.Repository
import com.example.reservoir_near_you.viewModelFactories.MagasinViewModelFactory
import com.example.reservoir_near_you.viewModels.MagasinViewModel
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.fragment_magasin.view.*

class MagasinFragment : Fragment() {

    private lateinit var viewModel: MagasinViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var binding: FragmentMagasinBinding

/*    BarChart Code*/
    lateinit var barList : ArrayList<BarEntry>
    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData

/*    PieChart Code*/
    private lateinit var pieList : ArrayList<PieEntry>
    private lateinit var pieDataSet:PieDataSet
    private lateinit var pieData:PieData

    val args: MagasinFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_magasin,
            container,
            false
        )

        setHasOptionsMenu(true)
        val repository = Repository()
        val viewModelFactory = MagasinViewModelFactory(repository)
        val magasinId = args.magasinId
        Log.d("maga", magasinId.toString())
        viewModel = ViewModelProvider(this, viewModelFactory)[MagasinViewModel::class.java]
        viewModel.getMagasin(magasinId)
        viewModel.magasinRespone.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful){

                Log.d("testwow", response.body()?.Magasin?.get(0)?.name.toString())
          /*      Log.d("testwow", response.body()?.name.toString())*/
/*                BarChart Code*/
                Log.d("wow", "det funker ")
                barList= ArrayList()
                response.body()?.Magasin?.get(0)?.fyllingsgrad
                    ?.let { BarEntry(1f, it) }?.let { barList.add(it) }

                response.body()?.Magasin?.get(0)?.fyllingsgrad_forrige_uke?.let { BarEntry(3f, it.toFloat()) }
                    ?.let { barList.add(it) }
                barDataSet= BarDataSet(barList, "Vann Magasin")
                barData= BarData(barDataSet)
                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
                binding.barChart.bar_chart.data=barData
                binding.barChart.bar_chart.description.text = response.body()?.Magasin?.get(0)?.name.toString()
                binding.barChart.bar_chart.description.textSize =13f
                barDataSet.valueTextColor= Color.BLACK
                barDataSet.valueTextSize=15f
                binding.barChart.bar_chart.setDrawValueAboveBar(true)



/*                PieChart code*/
                pieList = ArrayList()
                pieList.add(PieEntry(1f,200f))
                pieList.add(PieEntry(2f,100f))
                pieDataSet= PieDataSet(pieList, "TWH Kapasitet")
                pieData= PieData(pieDataSet)
                pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS,250)
                binding.lineChart.line_chart.data=pieData
                binding.lineChart.line_chart.description.text = "Twh DATA";
                binding.lineChart.line_chart.description.textSize =13f
                pieDataSet.valueTextColor= Color.BLACK
                pieDataSet.valueTextSize=15f


            }

        })

        binding.magasinViewModel = viewModel

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.map -> {
                val action = MagasinFragmentDirections.actionMagasinFragmentToMapsFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            R.id.login_logout -> {
                AuthUI.getInstance().signOut(requireContext())
                val action = MagasinFragmentDirections.actionMagasinFragmentToMainFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        })
    }

}