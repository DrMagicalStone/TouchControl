package xyz.magicalstone.touchcontrol

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import xyz.magicalstone.touchcontrol.databinding.ActivityMainBinding
import xyz.magicalstone.touchcontrol.skill.Skill
import xyz.magicalstone.touchcontrol.skill.SkillRegistry
import xyz.magicalstone.touchcontrol.skill.service.ServiceSkillCollector

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var collector: ServiceSkillCollector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            run {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

                //Demo code
                val contactWith: MutableMap<String, String> = HashMap()
                contactWith["contactTo"] = "x"
                Thread {
                    try {
                        Thread.sleep(2000)
                        val wechatCall = collector.importedSkills.getSkillById("xyz.magicalstone.touchcontrol.WeChatCall")
                        println("Wechat call$wechatCall")
                        wechatCall?.active(contactWith, Skill.ActivatorType.NON_AI)
                    } catch (e: InterruptedException) {
                        throw RuntimeException(e)
                    }
                }.start()
            }
        }

        //Demo code
        collector = ServiceSkillCollector(this)
        collector.refreshImportedSkills()
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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}