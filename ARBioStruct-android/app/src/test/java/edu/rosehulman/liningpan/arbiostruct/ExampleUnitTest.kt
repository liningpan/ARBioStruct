package edu.rosehulman.liningpan.arbiostruct

import edu.rosehulman.liningpan.arbiostruct.detailprotein.ProteinInfoService
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        ProteinInfoService.fetchPDBDescription(Protein("LDH", "1I10"))
    }
}
