package com.janosgyerik.soundbox

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    /**
     * The [android.support.v4.view.PagerAdapter] that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * [android.support.v4.app.FragmentStatePagerAdapter].
     */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        container.adapter = mSectionsPagerAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int {
            // Show 1 total pages.
            return 1
        }
    }

    data class SectionManager(val sections: List<Section>)

    data class Section(val title: String, val buttons: List<SoundButton>)

    data class SoundButton(val imagePath: String, val soundPath: String)

    /**
     * A placeholder fragment containing a simple view.
     */
    class PlaceholderFragment : Fragment() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            val rootView = inflater.inflate(R.layout.fragment_main, container, false)
            val exampleText = getString(R.string.section_format, arguments.getInt(ARG_SECTION_NUMBER))

            val sections = SectionManager(Arrays.asList(
                    //makeDummySection("Secondary weapons", 4),
                    sectionFromAssetDir("files/primary", "Primary weapons"),
                    sectionFromAssetDir("files/secondary", "Secondary weapons")
            ))

            val sectionsLayout = rootView.findViewById<LinearLayout>(R.id.sections)

            val opts = BitmapFactory.Options()
            opts.inDensity = DisplayMetrics.DENSITY_MEDIUM

            val typeface = Typeface.createFromAsset(context.assets, "fonts/StarWars.ttf")

            for (section in sections.sections) {
                val sectionView = inflater.inflate(R.layout.section, container, false)
                sectionsLayout.addView(sectionView)

                val title = sectionView.findViewById<TextView>(R.id.title)
                title.text = section.title
                title.typeface = typeface

                val buttonsLayout = sectionView.findViewById<LinearLayout>(R.id.buttons)

                for (button in section.buttons) {
                    val imageButton = ImageButton(this.context)
                    buttonsLayout.addView(imageButton)
                    val inputStream = context.assets.open(button.imagePath)
                    val image = Drawable.createFromResourceStream(resources, null, inputStream, null, opts)
                    imageButton.setImageDrawable(image)
                    imageButton.setBackgroundColor(Color.BLACK)

                    val afd = context.assets.openFd(button.soundPath)

                    imageButton.setOnClickListener { view ->
                        val mp = MediaPlayer()
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength())
                        mp.prepare()
                        mp.start()
                    }
                }
            }

            return rootView
        }

        private fun makeDummySection(title: String, count: Int): Section {
            val buttons = arrayListOf<SoundButton>()
            for (i in 1..count) {
                buttons.add(SoundButton("files/primary/rebel_btn.png", "files/primary/rebel_btn.mp3"))
            }
            return Section(title, buttons)
        }

        private fun sectionFromAssetDir(basedir: String, title: String): Section {
            val buttons = arrayListOf<SoundButton>()
            for (filename in context.assets.list(basedir)) {
                if (!filename.endsWith("_btn.png")) {
                    continue
                }
                val imagePath = basedir + "/" + filename
                val soundPath = basedir + "/" + filename.substring(0, filename.length - 4) + ".mp3"
                buttons.add(SoundButton(imagePath, soundPath))
            }
            return Section(title, buttons)
        }

        companion object {
            /**
             * The fragment argument representing the section number for this
             * fragment.
             */
            private val ARG_SECTION_NUMBER = "section_number"

            /**
             * Returns a new instance of this fragment for the given section
             * number.
             */
            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }
        }
    }
}
