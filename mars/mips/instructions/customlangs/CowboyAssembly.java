package mars.mips.instructions.customlangs;
import mars.simulator.*;
import mars.mips.hardware.*;
import mars.mips.instructions.syscalls.*;
import mars.*;
import mars.util.*;
import java.util.*;
import java.io.*;
import mars.mips.instructions.*;
import java.util.Random;
import java.util.Scanner;



public class CowboyAssembly extends CustomAssembly{
    SyscallReadInt syscallReadInt = new SyscallReadInt();
    @Override
    public String getName(){
        return "Cowboy Assembly";
    }

    @Override
    public String getDescription(){
        return "Simulate being a cowboy in the wild west!";
    }

    @Override
    protected void populate(){
        int zero = 0;
        for (int i = 16; i < 24; i++){
            RegisterFile.updateRegister(i, zero);
        }
        instructionList.add(
                new BasicInstruction("col $t1,50",
                        "Collect : Increase the amount of ($t1) by an immediate value",
                        BasicInstructionFormat.I_FORMAT,
                        "001000 fffff 00000 ssssssssssssssss",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[0]);
                                int add2 = operands[1] << 16 >> 16;
                                int sum = add1 + add2;
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));
         instructionList.add(
                new BasicInstruction("r target",
                        "Ride : Ride on your trusty steed to the designated label",
                        BasicInstructionFormat.J_FORMAT,
                        "000010 ffffffffffffffffffffffffff",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                Globals.instructionSet.processJump(
                                        ((RegisterFile.getProgramCounter() & 0xF0000000)
                                                | (operands[0] << 2)));
                            }
                        }));
          instructionList.add(
                new BasicInstruction("gssip",
                        "Gossip : Spread the word and carry out the system call specified by value in $v0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Globals.instructionSet.findAndSimulateSyscall(RegisterFile.getValue(2),statement);
                            }
                        }));
          instructionList.add(
                new BasicInstruction("ro $t1,$t2,label",
                        "Ride On : If $t1 and $t2 are equal, ride on to the target label",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000100 fffff sssss tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();

                                if (RegisterFile.getValue(operands[0]) == RegisterFile.getValue(operands[1])) {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));
        instructionList.add(
                new BasicInstruction("rt $t1,$t2,label",
                        "Ride Tall : Ride tall and branch to statement at label's address if $t1 is greater than $t2",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000111 fffff sssss tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                             {
                                int[] operands = statement.getOperands();
                                int value = RegisterFile.getValue(operands[1]);
                                if (RegisterFile.getValue(operands[0]) > value)
                                {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
               }));
        instructionList.add(
                new BasicInstruction("rs $t1,$t2,label",
                        "Ride Short : Ride short and branch to statement at label's address if $t1 is less than $t2",
                        BasicInstructionFormat.I_BRANCH_FORMAT,
                        "000001 fffff sssss tttttttttttttttt",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                             {
                                int[] operands = statement.getOperands();
                                int value = RegisterFile.getValue(operands[1]);
                                if (RegisterFile.getValue(operands[0]) < value)
                                {
                                    Globals.instructionSet.processBranch(operands[2]);
                                }
                            }
                        }));
           instructionList.add(
                new BasicInstruction("haul $t1,$t2",
                        "Haul : copy the value in $t2 into $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 sssss ttttt fffff 00000 100000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                int[] operands = statement.getOperands();
                                int add1 = RegisterFile.getValue(operands[0]);
                                int add2 = RegisterFile.getValue(operands[1]);
                                int sum = add1 + add2;
                                // overflow on A+B detected when A and B have same sign and A+B has other sign.
                                if ((add1 >= 0 && add2 >= 0 && sum < 0)
                                        || (add1 < 0 && add2 < 0 && sum >= 0))
                                {
                                    throw new ProcessingException(statement,
                                            "arithmetic overflow",Exceptions.ARITHMETIC_OVERFLOW_EXCEPTION);
                                }
                                RegisterFile.updateRegister(operands[0], sum);
                            }
                        }));
           instructionList.add(
                new BasicInstruction("chkh $t1",
                        "Check Honor : print honor value in $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 100011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t1 stores honor
                                int[] operands = statement.getOperands();
                                int honor = RegisterFile.getValue(operands[0]);
                                SystemIO.printString("Your honor is: " + honor + "\n");
                            }
                        }));
           instructionList.add(
                new BasicInstruction("chkm $t0",
                        "Check Money : print money value in $t0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 101011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t0 stores money
                                int[] operands = statement.getOperands();
                                int money = RegisterFile.getValue(operands[0]);
                                SystemIO.printString("You have: $" + money + "\n");
                            }
                        }));
           instructionList.add(
                new BasicInstruction("chkp $t2",
                        "Check Posse : print amount of posse members stored in $t2",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 111011",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t2 stores posse members
                                int[] operands = statement.getOperands();
                                int posse = RegisterFile.getValue(operands[0]);
                                SystemIO.printString("You have: " + posse + " members\n");
                            }
                        }));
           instructionList.add(
                new BasicInstruction("chkhe $t3",
                        "Check Health : print amount of health stored in $t3",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 111010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t3 stores health
                                int[] operands = statement.getOperands();
                                int health = RegisterFile.getValue(operands[0]);
                                SystemIO.printString("Your health is at: " + health + "\n");
                            }
                        }));
            instructionList.add(
                new BasicInstruction("chkd $t4",
                        "Check Dead Eye : print amount of dead eye stored in $t4",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 111110",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t4 stores dead eye
                                int[] operands = statement.getOperands();
                                int deadEye = RegisterFile.getValue(operands[0]);
                                SystemIO.printString("Your dead eye is at: " + deadEye + "\n");
                            }
                        }));
           instructionList.add(
                new BasicInstruction("flee",
                        "Flee: flee the region, reset you honor in $t1 to 0",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 010101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t1 stores honor
                                int zeroed = 0;
                                SystemIO.printString("You fled the region...no one knows you! Your honor is reset to 0\n");
                                RegisterFile.updateRegister(9, zeroed);
                            }
                        }));
           instructionList.add(
                new BasicInstruction("abdnp",
                        "Abandon Posse: you abandoned your posse, reset your posse members in $t2 to 0 and lower honor in $t1 by 3",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 101010",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t1 stores honor and $t2 stores posse members
                                int honor = RegisterFile.getValue(9);
                                int minus = honor - 3;
                                int zeroed = 0;
                                SystemIO.printString("You abandoned your posse...They are hurt by your actions... You now have 0 posse members and you honor decreased by 3\n");
                                RegisterFile.updateRegister(9, minus);
                                RegisterFile.updateRegister(10, zeroed);
                            }
                        }));
           instructionList.add(
                new BasicInstruction("fght",
                        "Fight: you started a fight, lower your health in $t2 by a random amount between 0-100",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 111000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t3 stores health
                                Random random = new Random();
                                int randomNum = random.nextInt(101);
                                int damage = randomNum;
                                int health = RegisterFile.getValue(11);
                                int minus = health - damage;
                                int honor = RegisterFile.getValue(9);
                                int minusH = honor - 3;
                                int one = 1;
                                if (damage < 26){
                                    SystemIO.printString("You started a fight and...Won! Still hurt though... You lost " + damage + " health and 3 honor\n");
                                }else{
                                    SystemIO.printString("You started a fight and...Lost?! Ouch... You lost " + damage + " health and 3 honor\n");
                                }
                                if(health - damage <= 0){
                                    SystemIO.printString("You lost too much health.. you passed out! Someone brought you to a doctor...your health is now at 1.\n");
                                    RegisterFile.updateRegister(11, one);
                                    RegisterFile.updateRegister(9, minusH);
                                    return;
                                }else{
                                    RegisterFile.updateRegister(11, minus);
                                    RegisterFile.updateRegister(9, minusH);
                                }

                            }
                        }));
           instructionList.add(
                new BasicInstruction("rob",
                        "Rob: you robbed an innocent bystander, lower your dead eye in $t4 by 4, lower honor in $t1 by 50, and gain a random amount of money. %50 chance of success.",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 111000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t3 stores health
                                Random random = new Random();
                                int randomOutcome = random.nextInt(2);
                                int outcome = randomOutcome;
                                Random randomTwo = new Random();
                                int randomReward = randomTwo.nextInt(31);
                                int reward = randomReward;
                                int honor = RegisterFile.getValue(9);
                                int minusH = honor - 30;
                                int deadEye = RegisterFile.getValue(12);
                                int lowerDE = deadEye - 4;
                                int money = RegisterFile.getValue(11);
                                int plusMoney = money + reward;
                                if (deadEye - lowerDE <= 0){
                                    SystemIO.printString("You don't have enough Dead Eye to rob someone, drink some snake oil first!\n");
                                }
                                if (outcome == 0){
                                    SystemIO.printString("You tried to rob someone but the law came! Maybe next time? You spent 4 Dead Eye and lost 30 honor.\n");
                                }else{
                                    SystemIO.printString("You robbed someone! They had $" + reward + "! You spent 4 Dead Eye and lost 30 honor.\n");
                                    RegisterFile.updateRegister(8, plusMoney);  
                                }
                                RegisterFile.updateRegister(12, lowerDE);
                                RegisterFile.updateRegister(9, minusH);

                            }
                        }));
        instructionList.add(
                new BasicInstruction("jobl",
                        "Job Listing: you posted a job listing for posse members and gained 2/4 posse members depending on your honor",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 110100",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t2 stores posse members
                                int honor = RegisterFile.getValue(9);
                                int members = RegisterFile.getValue(10);
                                int membersHighHonor = members + 2;
                                int membersLowHonor = members + 4;
                                if (honor > 0){
                                    SystemIO.printString("You posted a job listing... They liked how honorable you are! Two trustworthy people joined your posse!\n");
                                    RegisterFile.updateRegister(10, membersHighHonor);
                                }else{
                                    SystemIO.printString("You posted a job listing... They liked how infamous you are...4 untrustworthy people joined your posse...\n");
                                    RegisterFile.updateRegister(10, membersLowHonor);
                                }
                            }
                        }));
        instructionList.add(
                new BasicInstruction("bnty",
                        "Bounty: you took on a bounty, if you bring them in alive you gain more money and honor, if not you lose honor and gain less money",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 110000",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t0 stores money, $t1 stores honor
                                    int honor = RegisterFile.getValue(9);
                                    int money = RegisterFile.getValue(8);
                                    int deadEye = RegisterFile.getValue(12);
                                    int highHonor = honor + 50;
                                    int lowHonor = honor - 50;
                                    int HHmoney = money + 100;
                                    int LHmoney = money + 50;
                                    int lowerDE = deadEye - 20;
                                    if(deadEye - 20 <= 0){
                                        SystemIO.printString("You don't have enough dead eye to complete a bounty... Drink snake oil!\n");
                                        return;
                                    }
                                    SystemIO.printString("Do you wanna bring in your bounty 1.) dead or 2.) alive? Type 1 or 2.\n");
                                    int value = 0;
                                    try
                                    {
                                    value = SystemIO.readInteger(syscallReadInt.getNumber());
                                    } 
                                        catch (NumberFormatException e)
                                    {
                                    throw new ProcessingException(statement,
                                        "invalid integer input (syscall "+syscallReadInt.getNumber()+")",
			                            Exceptions.SYSCALL_EXCEPTION);
                                    }
                                    RegisterFile.updateRegister(2, value);
                                    int input = RegisterFile.getValue(2);
                                    if (input == 1){
                                        SystemIO.printString("You brought in your bounty dead... You lost 50 honor, used 20 dead eye, and got $50...\n");
                                        RegisterFile.updateRegister(9, lowHonor);
                                        RegisterFile.updateRegister(8, LHmoney);
                                        RegisterFile.updateRegister(12, lowerDE);
                                    }else if(input == 2){
                                        SystemIO.printString("You brought in your bounty alive! You gained 50 honor, used 20 dead eye, and got $100!\n");
                                        RegisterFile.updateRegister(9, highHonor);
                                        RegisterFile.updateRegister(8, HHmoney);
                                        RegisterFile.updateRegister(12, lowerDE);
                                    }
                                }
                        }));

            instructionList.add(
                new BasicInstruction("satchel",
                    "Satchel: you open your satchel and can choose which item to use!",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 000101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t3 stores health
                                SystemIO.printString("You open your satchel... let's see whats inside:\n");
                                int health = RegisterFile.getValue(11);
                                int deadEye = RegisterFile.getValue(12);
                                for (int i = 16; i < 24; i++){
                                            int slotVal = RegisterFile.getValue(i);
                                            if (slotVal == 0){
                                                continue;
                                            }
                                            if (slotVal != 0){
                                                if(slotVal == 1){
                                                    SystemIO.printString("  - 1. You have a Potent Miracle Tonic [+ 20 H, + 20 DE]\n");
                                                }
                                                if(slotVal == 2){
                                                    SystemIO.printString("  - 2. You have a Potent Health Tonic [+ 20 H]\n");
                                                }
                                                if(slotVal == 3){
                                                    SystemIO.printString("  - 3. You have a Health Tonic [+ 10 H]\n");
                                                }
                                                if(slotVal == 4){
                                                    SystemIO.printString("  - 4. You have Potent Snake Oil [+ 20 DE]\n");
                                                }
                                                if(slotVal == 5){
                                                    SystemIO.printString("  - 5. You have Snake Oil [+ 10 DE]\n");
                                                }
                                            }
                                }
                                SystemIO.printString("Which tonic do you want to use? Type the number next to the item and hit enter.\n");
                                int value = 0;
                                try
                                {
                                    value = SystemIO.readInteger(syscallReadInt.getNumber());
                                } 
                                catch (NumberFormatException e)
                                {
                                throw new ProcessingException(statement,
                                    "invalid integer input (syscall "+syscallReadInt.getNumber()+")",
			                        Exceptions.SYSCALL_EXCEPTION);
                                }
                                RegisterFile.updateRegister(2, value);
                                int input = RegisterFile.getValue(2);  
                                int hundred = 100;                                          
                                    if(input == 1){
                                        SystemIO.printString("You drank your Potent Miracle Tonic! Yuck... You regained 20 health and 20 dead eye!\n");
                                        int plus = health + 20;
                                        int add = deadEye + 20;
                                        if (health + plus > 100){
                                            RegisterFile.updateRegister(11, hundred);
                                        }else{
                                        RegisterFile.updateRegister(11, plus);
                                        }
                                        if (deadEye + add > 100){
                                            RegisterFile.updateRegister(12, hundred);
                                        }else{
                                        RegisterFile.updateRegister(12, add);
                                        }
                                    }
                                    if(input == 2){
                                        SystemIO.printString("You drank your Potent Health Tonic! Yuck... You regained 20 health!\n");
                                        int plus = health + 20;
                                        if (health + plus > 100){
                                            RegisterFile.updateRegister(11, hundred);
                                        }else{
                                        RegisterFile.updateRegister(11, plus);
                                        }                                    }
                                    if(input == 3){
                                        SystemIO.printString("You drank your Health Tonic! Yuck... You regained 10 health!\n");
                                        int plus = health + 10;
                                        if (health + plus > 100){
                                            RegisterFile.updateRegister(11, hundred);
                                        }else{
                                        RegisterFile.updateRegister(11, plus);
                                        }                                    }
                                    if(input == 4){
                                        SystemIO.printString("You drank your Potent Snake Oil! Yuck... You regained 20 dead eye!\n");
                                        int add = deadEye + 20;
                                        if (deadEye + add > 100){
                                            RegisterFile.updateRegister(12, hundred);
                                        }else{
                                        RegisterFile.updateRegister(12, add);
                                        }                                    }
                                    if(input == 5){
                                        SystemIO.printString("You drank your Snake Oil! Yuck... You regained 10 dead eye!\n");
                                        int add = deadEye + 10;
                                        if (deadEye + add > 100){
                                            RegisterFile.updateRegister(12, hundred);
                                        }else{
                                        RegisterFile.updateRegister(12, add);
                                        }                                    
                                    }                                          
                                }            
                        }));
            instructionList.add(
                new BasicInstruction("shop",
                        "Shop: Buy tonics to heal yourself, tonics bought are stored in satchel in s registers",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 001101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                    int money = RegisterFile.getValue(8);
                                    SystemIO.printString("You decide to go shop for tonics...You have $" + money + " in your satchel!\n");
                                    SystemIO.printString("Type the number that is next to the tonic you want to buy: \n1.) Potent Miracle Tonic, $3\n2.) Potent Health Tonic, $2\n3.) Health Tonic, $1\n4.) Potent Snake Oil, $2\n5.) Snake Oil, $1\n");

                                    int value = 0;
                                    try
                                    {
                                    value = SystemIO.readInteger(syscallReadInt.getNumber());
                                    } 
                                        catch (NumberFormatException e)
                                    {
                                    throw new ProcessingException(statement,
                                        "invalid integer input (syscall "+syscallReadInt.getNumber()+")",
			                            Exceptions.SYSCALL_EXCEPTION);
                                    }
                                    RegisterFile.updateRegister(2, value);
                                    int input = RegisterFile.getValue(2);
                                switch (input) {
                                    // for each case it cycles through s registers to find an empty slot and buys the tonic 
                                    // if empty slot is not found it says satchel is full
                                    case 1:
                                        if(money - 3 < 0){
                                            SystemIO.printString("You don't have enough money for that... Go get some!\n");
                                            return;
                                        }
                                        for (int i = 16; i < 24; i++){
                                            int slot = RegisterFile.getValue(i);
                                            if (slot != 0)
                                            continue;
                                            if (slot == 0){
                                                RegisterFile.updateRegister(i, input);
                                                int minus = money - 3;
                                                RegisterFile.updateRegister(8, minus);
                                                SystemIO.printString("You bought a Potent Miracle Tonic. When used it increases your health, and dead eye by 20!\n");
                                                return;
                                            }
                                        }
                                        SystemIO.printString("Your satchel is full!\n");
                                        break;
                                    case 2:
                                        if(money - 2 < 0){
                                            SystemIO.printString("You don't have enough money for that... Go get some!\n");
                                            return;
                                        }
                                        for (int i = 16; i < 24; i++){
                                            int slot = RegisterFile.getValue(i);
                                            if (slot != 0)
                                            continue;
                                            if (slot == 0){
                                                RegisterFile.updateRegister(i, input);
                                                int minus = money - 2;
                                                RegisterFile.updateRegister(8, minus);
                                                SystemIO.printString("You bought a Potent Health Tonic. When used it increases your health by 20!\n");
                                                return;
                                            }
                                        }
                                        SystemIO.printString("Your satchel is full!\n");
                                        break;
                                    case 3:
                                        if(money - 1 < 0){
                                            SystemIO.printString("You don't have enough money for that... Go get some!\n");
                                            return;
                                        }
                                        for (int i = 16; i < 24; i++){
                                            int slot = RegisterFile.getValue(i);
                                            if (slot != 0)
                                            continue;
                                            if (slot == 0){
                                                RegisterFile.updateRegister(i, input);
                                                int minus = money - 1;
                                                RegisterFile.updateRegister(8, minus);
                                                SystemIO.printString("You bought a Health Tonic. When used it increases your health by 10!\n");
                                                return;
                                            }
                                        }
                                        SystemIO.printString("Your satchel is full!\n");
                                        break;
                                    case 4:
                                        if(money - 2 < 0){
                                            SystemIO.printString("You don't have enough money for that... Go get some!\n");
                                            return;
                                        }
                                        for (int i = 16; i < 24; i++){
                                            int slot = RegisterFile.getValue(i);
                                            if (slot != 0)
                                            continue;
                                            if (slot == 0){
                                                RegisterFile.updateRegister(i, input);
                                                int minus = money - 2;
                                                RegisterFile.updateRegister(8, minus);
                                                SystemIO.printString("You bought Potent Snake Oil. When used it increases your dead eye by 20!\n");
                                                return;
                                            }
                                        }
                                        SystemIO.printString("Your satchel is full!\n");
                                        break;
                                    case 5:
                                        if(money - 1 < 0){
                                            SystemIO.printString("You don't have enough money for that... Go get some!\n");
                                            return;
                                        }
                                        for (int i = 16; i < 24; i++){
                                            int slot = RegisterFile.getValue(i);
                                            if (slot != 0)
                                            continue;
                                            if (slot == 0){
                                                RegisterFile.updateRegister(i, input);
                                                int minus = money - 1;
                                                RegisterFile.updateRegister(8, minus);
                                                SystemIO.printString("You bought Snake Oil. When used it increases your dead eye by 10!\n");                                                
                                                return;
                                            }
                                        }
                                        SystemIO.printString("Your satchel is full!\n");
                                        break;
                                }
                            }
                        }));
        instructionList.add(
                new BasicInstruction("gvd 50",
                        "Give Donation : You give a donation to charity and lose the amount of money specifie stored in $t0. You gain 50 honor.",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 fffff 00000 110101",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t3 stores health
                                int[] operands = statement.getOperands();
                                int money = RegisterFile.getValue(8);
                                int amount = operands[0] << 16 >> 16;
                                int minus = money - amount;
                                int honor = RegisterFile.getValue(9);
                                int plus = honor + 50;
                                if(money - amount <= 0){
                                    SystemIO.printString("You don't have enough money to donate... Go get money!\n");
                                    return;
                                }
                                SystemIO.printString("You donated " + operands[0] +" to charity and gained 50 honor!\n");
                                RegisterFile.updateRegister(8, minus);
                                RegisterFile.updateRegister(9, plus);
                            }
                        }));
        instructionList.add(
                new BasicInstruction("fhrs",
                        "Feed Horse: you feed your horse and gain 10 honor stored in $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 110111",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t1 stores honor
                                int honor = RegisterFile.getValue(9);
                                int plus = honor + 10;
                                SystemIO.printString("You fed your Horse! It thanks you with a neigh... You gained 10 honor!\n");
                                RegisterFile.updateRegister(9, plus);
                            }
                        }));
        instructionList.add(
                new BasicInstruction("bhrs",
                        "Brush Horse: you brush your horse and gain 10 honor stored in $t1",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 010111",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                // $t1 stores honor
                                int honor = RegisterFile.getValue(9);
                                int plus = honor + 10;
                                SystemIO.printString("You brushed your Horse! All nice and clean... You gained 10 honor!\n");
                                RegisterFile.updateRegister(9, plus);
                            }
                        }));
           instructionList.add(
                new BasicInstruction("hunt",
                        "Hunt: you hunt and return with a randomized animal",
                        BasicInstructionFormat.R_FORMAT,
                        "000000 00000 00000 00000 00000 100001",
                        new SimulationCode()
                        {
                            public void simulate(ProgramStatement statement) throws ProcessingException
                            {
                                Random random = new Random();
                                int randomNum = random.nextInt(8);
                                RegisterFile.updateRegister(2, randomNum);
                                int deadEye = RegisterFile.getValue(12);
                                int lowerDE = deadEye - 10;
                                if(deadEye - 10 <= 0){
                                    SystemIO.printString("You don't have enough dead eye to hunt... Drink snake oil!\n");
                                    return;
                                }
                                RegisterFile.updateRegister(12, lowerDE);
                                switch (RegisterFile.getValue(2)) {
                                    case 0:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Deer!\n");
                                        break;
                                    case 1:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Shrew?\n");
                                        break;
                                    case 2:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Rabbit...!\n");
                                        break;
                                    case 3:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Bison?! How?!\n");
                                        break;
                                    case 4:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Skunk... Can you eat that?\n");
                                        break;
                                    case 5:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Boar!\n");
                                        break;
                                    case 6:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....What? You didn't catch anything!\n");
                                        break;
                                    case 7:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Buck!\n");
                                        break;
                                    default:
                                        SystemIO.printString("You went hunting, used 10 dead eye and managed to catch a....Deer!\n");
                                }
                            }
                        }));

    }
}