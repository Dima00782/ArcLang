/*
    this is simple Arc program
*/

a = object
sleepr
sleep
dump a

a.x = object
a.y = object
a.z = object

dump a

b = object
b.x = a

dump b

b ~= a
a.x = object

// some usefull comment here


thread {
	sleep
	        sleepr

	c          ~=       a
}
thread        {
}

thread {}

thread {   }

