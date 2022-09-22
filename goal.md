```swift
class mc_lang {

    var tick_count = 0

    func test() {
        return 10 % 5
    }

    func add_nums(param0, param1) {
        return param0 + param1
    }

    func no_return(param0) {
        sout('Here is a thing!')
        sout('Here is a thing with a param: ${param0}')
    }

    @Service("tick")
    func global_tick() {
        run('execute as @a[type=player] run function #custom:player_tick')
        sout('Called every tick!')
        tick_count += 1
        tick_count %= 3
        if (tick_count == 0) {
            sout('Called every 3 ticks!')
        }
    }
    
    @Service("custom:player_tick")
    func player_tick() {
        sout('Called every tick for players!')
    }
    
    func return_tests() {
        
    }
}
```

output

```
project/startup.mcfunction
scoreboard objectives add project_variables dummy
```

```
project/mc_lang/test.mcfunction
scoreboard players set $project/mc_lang/test/return project_variables 0
scoreboard players operation $project/mc_lang/test/return project_variables = 10 project_constants
scoreboard players operation $project/mc_lang/test/return project_variables %= 5 project_constants
```

```
project/mc_lang/add_nums.mcfunction
scoreboard players set $project/mc_lang/add_nums/return project_variables 0
scoreboard players operation $project/mc_lang/add_nums/return project_variables = $project/mc_lang/add_nums/param0 project_params
scoreboard players operation $project/mc_lang/add_nums/return project_variables += $project/mc_lang/add_nums/param1 project_params
```

```
project/mc_lang/no_return.mcfunction
tellraw @a "Here is a thing!"
tellraw @a "[{\"text\":\"Here is a thing with a param: \"}, {\"score\":{\"name\":\"$project/mc_lang/no_return/param0\",\"objective\":\"project_params\"}]"
```

class mc_lang {

    func test() {

        return 10 % 5
    }

    func add_nums(param0, param1) {
        return param0 + param1
    }

    func no_return(param0) {
        sout("Here is a thing!")
        sout("Here is a thing with a param: ${param0}")
    }
}