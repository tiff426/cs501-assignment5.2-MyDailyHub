package com.example.mydailyhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.mydailyhub.ui.theme.MyDailyHubTheme
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.tween
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

class MainActivity : ComponentActivity() {
    private val viewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyDailyHubTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//                NotesScreen(viewModel=viewModel)
//                TasksScreen(viewModel=viewModel)
//                CalendarScreen()
                MyApp(viewModel = viewModel)
            }
        }
    }
}

class MyViewModel : ViewModel() {
    // for persistent recipes?
    val notes: SnapshotStateList<String> = mutableStateListOf()
    val tasks: SnapshotStateList<Task> = mutableStateListOf()


    fun addNote(note : String) {
        notes.add(note)
    }

    fun addTask(task : Task) {
        tasks.add(task)
    }
}

//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MyDailyHubTheme {
//        Greeting("Android")
//    }
//}
//


//Design and build a mobile app that uses BottomNavigation and BottomNavigationItem to switch between three functional screens: Notes, Tasks, and Calendar.
//
//Requirements:
//• DONE Three screens: DONE Notes (text entries), DONE Tasks (checkbox list), DONECalendar (static placeholder)
//• DONE Bottom navigation bar implemented with BottomNavigation and dynamic highlighting using currentBackStackEntryAsState()
//• DONE Use sealed route objects and manage screen transitions with popUpTo, launchSingleTop, and restoreState = true
//• DONE Each screen must maintain its state properly across recompositions (via ViewModel or hoisting)
//• Show how navigation impacts the backstack behavior (test and document back button behavior)
//• DONE Add icons to BottomNavigationItem using Icons.Default.*; Animate screen transitions using navigation arguments

// nav forms
sealed class Routes(val route : String, val title : String, val icon : ImageVector) {
    object Home : Routes("home", "Home", Icons.Default.Home)
    object Notes : Routes("notes", "Notes", Icons.Default.Create)
    object Tasks : Routes("tasks", "Tasks", Icons.Default.Menu)
    object Calendar : Routes("calendar", "Calendar", Icons.Default.DateRange)
}

val bottomNavScreens = listOf(
    Routes.Home,
    Routes.Notes,
    Routes.Tasks,
    Routes.Calendar
)

// main screen
@Composable
fun MyApp(viewModel: MyViewModel = MyViewModel()) {
    // create navController instance
    val navController = rememberNavController()

    // bottom bar + scaffold
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                bottomNavScreens.forEach { screen ->
                    NavigationBarItem(
                        label = { Text(screen.title) }, // The text label for the item.
                        icon = {
                            Icon(
                                screen.icon,
                                contentDescription = screen.title
                            )
                        }, // The icon for the item.

                        // 5. Determine if this item is currently selected.
                        // We check if the current route is part of the destination's hierarchy.
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,

                        // 6. Define the click action for the item.
                        onClick = {
                            // This is the core navigation logic.
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true // Save the state of the screen you're leaving.
                                }
                                // Avoid multiple copies of the same destination when re-selecting the same item.
                                launchSingleTop = true
                                // Restore state when re-selecting a previously selected item.
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 7. Define the NavHost, which is the container for our screen content.
        // The content of the NavHost changes based on the current route.
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route, // The first screen to show.
            modifier = Modifier.padding(innerPadding) // Apply padding from the Scaffold.
        ) {
            // Define a composable for each screen in our navigation graph.
            composable(route = "home",
                enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { it }) },
                exitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { -it }) }
            ) {
                HomeScreen(navController = navController)
            }
            composable(Routes.Notes.route,
                enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { it }) },
                exitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { -it }) }
            ) {
                NotesScreen(navController = navController, viewModel)
            }
            composable(Routes.Tasks.route,
                enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { it }) },
                exitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { -it }) }
            ) { TasksScreen(viewModel) }
            composable(Routes.Calendar.route,
                enterTransition = { fadeIn() + slideInHorizontally(initialOffsetX = { it }) },
                exitTransition = { fadeOut() + slideOutHorizontally(targetOffsetX = { -it }) }
            ) { CalendarScreen() }
        }
    }
}

// Making my screens first

// should probabaly makeuse generic screens for home + cal
@Composable
fun HomeScreen(navController : NavController) {
//     just a placeholder
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Center the content horizontally and vertically.
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Home", style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Composable
fun NotesScreen(navController: NavController, viewModel: MyViewModel) {
    val temp = listOf(
        "help",
        "help me",
        "HELP"
    )
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Notes")
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(viewModel.notes) { index, note ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color.LightGray
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = note) // show the note
                }
            }
        }
        Text(text = "add a note")
        var note by rememberSaveable { mutableStateOf("") }
        TextField(
            value = note,
            onValueChange = { note = it },
            placeholder = { Text(text = "start writing...") }
        )
        Button(
            onClick = {
                // add the note to the list (CHANGE LATER FOR VIEW MODEL/PERSISTENCE)
                viewModel.addNote(note)
                note = "" // set the input back to empty
            }
        ) {
            Text(text = "add note")
        }
    }
}

// need to have task object so each tasl can have its own checked var
data class Task(val text: String) {
    var checked by mutableStateOf(false)
}

@Composable
fun TaskItem(task : Task) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.checked,
            onCheckedChange = { isChecked ->
                task.checked = isChecked // Update the task's completion status
            }
        )
        Text(
            text = task.text,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun TasksScreen(viewModel: MyViewModel) {
    var task by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text("To do")
        LazyColumn() {
            itemsIndexed(viewModel.tasks) { index, task ->
                TaskItem(task = task)
            }
        }
        Text("add task")
        TextField(
            value = task,
            onValueChange = { task = it },
            placeholder = { Text(text = "start writing...") }
        )
        Button(onClick = {
            viewModel.addTask(Task(task))
            task = ""
        }) {
            Text(text="add task")
        }
    }
}

@Composable
fun CalendarScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Center the content horizontally and vertically.
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, modifier = Modifier.padding(bottom = 8.dp))
            Text(text = "Calendar", style = MaterialTheme.typography.headlineMedium)
        }
    }
}
