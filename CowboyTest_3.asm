# set our starting stats
col $t0, 100 # money
col $t1, 0   # honor
col $t2, 0   # posse
col $t3, 100 # health
col $t4, 100 # dead eye

# gain 10 honor by feeding your horse
fhrs

# manually print honor by moving 1 into $v0 to say we are 
# printing an int and then moving our honor value into $a0 
# and then using gssip (syscall)
col $v0, 1
haul $a0, $t1
gssip
