# set our starting stats
col $t0, 100 # money
col $t1, 0   # honor
col $t2, 0   # posse
col $t3, 100 # health
col $t4, 100 # dead eye

# set extra placeholder number, change to 0 for high honor route, 1 for low honor route
col $t5, 0

# check to see if our honor is equal to 0, 
# if it is then care for your horse to gain some. 
# If it is not, jump to a low honor route
ro $t1, $t5, HorseCare
r LowHonorRoute

# gain 20 honor by feeding and brushing your horse, then print your honor afterwards
HorseCare:
bhrs
fhrs
chkh $t1

# if honor is higher than 0 now, jump to the high honor route, if not jump to the lower honor route
rt $t1, $t5, HighHonorRoute
r LowHonorRoute

# Take in a bounty alive, print your money, hunt animals, give 10 dollars to charity, 
# gain trustworthy posse members, print money again, then jump to the end
HighHonorRoute:
bnty
chkm $t0
hunt
gvd 10
jobl
chkm $t0
r End

# start a fight and lose honor and health, print your health, take in a bounty dead, 
# gain untrustworthy posse members, check how many members you have
LowHonorRoute:
fght
chkhe $t3
bnty
jobl
chkp $t2
abdnp
flee
chkp $t2

# print honor
End:
chkh $t1
