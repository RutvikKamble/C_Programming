#include<iostream>
using namespace std;

class Arithmatic            //encapsulation
{
    public:                 //access specifier
        int iNo1;
        int iNo2;

        Arithmatic()
        {
            cout<<"Inside default Constructer";
            iNo1 = 0;
            iNo2 = 0;
        }

        Arithmatic(int A, int B)
        {
            cout<<"Inside paramatrized Constructer";
            iNo1 = A;
            iNo2 = B;
        }

        int Addition()      //behaviour
        {
            int iAns = 0;
            iAns = iNo1 + iNo2;
            return 0;
        }

        int Substraction()
        {
            int iAns = 0;
            iAns = iNo1 - iNo2;
            return 0;
        }
};

int main()
{
    Arithmatic aobj1(11,10);
    Arithmatic aobj2(21,20);

    int iRet = 0;

    iRet = aobj1.Addition();
    cout<<"Additon is :"<<iRet<<"\n";

    iRet = aobj1.Substraction();
    cout<<"Substraction is :"<<iRet<<"\n";

    iRet = aobj2.Addition();
    cout<<"Additon is :"<<iRet<<"\n";

    iRet = aobj2.Substraction();
    cout<<"Substraction is :"<<iRet<<"\n";

    return 0;
}


//What you want to do
//How to do

