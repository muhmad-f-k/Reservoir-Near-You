package com.example.reservoir_near_you.screen

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.reservoir_near_you.R
import com.example.reservoir_near_you.databinding.FragmentMagasinBinding
import com.example.reservoir_near_you.repository.Repository
import com.example.reservoir_near_you.viewModelFactories.MagasinViewModelFactory
import com.example.reservoir_near_you.viewModels.MagasinViewModel
import com.firebase.ui.auth.AuthUI
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
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
    private lateinit var pieList : ArrayList<BarEntry>
    private lateinit var pieDataSet:BarDataSet
    private lateinit var pieData:BarData

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
        val magasinNavn = args.magasinNavn
        viewModel = ViewModelProvider(this, viewModelFactory)[MagasinViewModel::class.java]
        viewModel.getMagasin(magasinNavn)
        viewModel.magasinRespone.observe(viewLifecycleOwner, Observer { response ->
            if (response.isSuccessful){

                val xAxisLabel: ArrayList<String> = ArrayList()
                xAxisLabel.add("Fyllingsgrad")
                xAxisLabel.add("Fyllingsgrad forrige uke")
                xAxisLabel.add("")
                xAxisLabel.add("")
                xAxisLabel.add("")
                xAxisLabel.add("")
                xAxisLabel.add("")


                val xAxis: XAxis = binding.barChart.bar_chart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE

                val formatter: ValueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return xAxisLabel[value.toInt()]
                    }
                }

                xAxis.granularity = 1f // minimum axis-step (interval) is 1

                xAxis.valueFormatter = formatter





                barList= ArrayList()
                response.body()?.Magasin?.find { it.name == magasinNavn }
                    ?.let { BarEntry(0f, it.fyllingsgrad) }?.let { barList.add(it) }
                response.body()?.Magasin?.find { it.name == magasinNavn }
                    ?.let { BarEntry(1f, it.fyllingsgrad_forrige_uke) }?.let { barList.add(it) }
//                response.body()?.Magasin?.find { it.name == magasinNavn }
//                    ?.let { BarEntry(2f, it.endring_fyllingsgrad) }?.let { barList.add(it) }




                response.body()?.Magasin?.find { it.name == magasinNavn }?.name

                barDataSet= BarDataSet(barList, "Vann Magasin")
                barData= BarData(barDataSet)
                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 250)
                binding.barChart.bar_chart.data=barData
//                binding.barChart.bar_chart.description.text = response.body()?.Magasin?.find { it.name == magasinNavn }?.name
                binding.barChart.bar_chart.description.textSize =13f
                barDataSet.valueTextColor= Color.BLACK
                barDataSet.valueTextSize=15f
/*                binding.barChart.bar_chart.setDrawValueAboveBar(true)*/
                binding.barChart.bar_chart.animateY(10)
                binding.barChart.bar_chart.animateX(10)
                binding.barChart.bar_chart.description.isEnabled = false
                binding.barChart.bar_chart.axisLeft.axisMinimum=0f
                binding.barChart.bar_chart.axisLeft.axisMaximum=1.5f
                binding.barChart.bar_chart.axisRight.axisMinimum=0f
                binding.barChart.bar_chart.axisRight.axisMaximum=1.5f





/*                PieChart code*/


                pieList = ArrayList()
                response.body()?.Magasin?.find { it.name == magasinNavn }
                    ?.let { BarEntry(1f, it.kapasitet_TWh) }?.let { pieList.add(it) }
                response.body()?.Magasin?.find { it.name == magasinNavn }
                    ?.let { BarEntry(2f, it.fylling_TWh) }?.let { pieList.add(it) }

                pieDataSet= BarDataSet(pieList, "TWH Kapasitet")
                pieData= BarData(pieDataSet)
                pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS,250)
                binding.lineChart.line_chart.data=pieData
                binding.lineChart.line_chart.description.text = response.body()?.Magasin?.find { it.name == magasinNavn }?.name
                binding.lineChart.line_chart.description.textSize =13f
                pieDataSet.valueTextColor= Color.BLACK
                pieDataSet.valueTextSize=15f
                binding.lineChart.line_chart.animateY(10)
                binding.lineChart.line_chart.animateX(10)
                binding.lineChart.line_chart.axisLeft.axisMinimum=0f
                binding.lineChart.line_chart.axisLeft.axisMaximum=40f
                binding.lineChart.line_chart.axisRight.axisMinimum=0f
                binding.lineChart.line_chart.axisRight.axisMaximum=40f
                binding.lineChart.line_chart.description.setPosition(50f,50f)


            }

        })

        binding.magasinViewModel = viewModel

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.login).isVisible = false
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when(item.itemId) {
            R.id.map -> {
                val action = MagasinFragmentDirections.actionMagasinFragmentToMapsFragment()
                view?.findNavController()?.popBackStack()
                true
            }
            R.id.logout -> {
                AuthUI.getInstance().signOut(requireContext())
                val action = MagasinFragmentDirections.actionMagasinFragmentToMainFragment()
                view?.findNavController()?.navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        })
    }

}