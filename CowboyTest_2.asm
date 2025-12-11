# set our starting stats
col $t0, 100 # money
col $t1, 0   # honor
col $t2, 0   # posse members
col $t3, 100 # health
col $t4, 100 # dead eye

# set extra placeholder number
col $t5, 50

# rob, check how much money we have, and then start a fight
rob
chkm $t0
fght

# if our health is less than 50, jump to our regain health segment. If not, jump to the end
rs $t3, $t5, Regain_Health
r End1

# buy a tonic and use tonic to regain health. Print our health
Regain_Health:
shop
satchel
chkhe $t3
r End2

# print our health
End1:
chkhe $t3

# second ending in case we didn't lose a lot of health, just print our honor
End2:
chkh $t1


