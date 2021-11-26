import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fernandocejas.sample.features.login.HistoryDayFragment
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HistoryPageAdapter(fm:FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3;
    }

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> {
                return HistoryDayFragment.newInstance(ZonedDateTime.now())
            }
            1 -> {
                return HistoryDayFragment.newInstance(ZonedDateTime.now().minusDays(1))
            }
            2 -> {
                return HistoryDayFragment.newInstance(ZonedDateTime.now().minusDays(2))
            }
            else -> {
                return HistoryDayFragment.newInstance(ZonedDateTime.now().minusDays(3))
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> {
                return "Vandaag"
            }
            1 -> {
                return "Gisteren"
            }
            2 -> {
                return ZonedDateTime.now().minusDays(2).format(DateTimeFormatter.ISO_LOCAL_DATE)
            }
        }
        return super.getPageTitle(position)
    }

}