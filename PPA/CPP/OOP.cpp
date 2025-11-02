#include<iostream>
using namespace std;

class Arithmatic            //encapsulation
{
    public:                 //access specifier
        int iNo1;
        int iNo2;

        Arithmatic()
        {
            iNo1 = 0;
            iNo2 = 0;
        }

        Arithmatic(int A, int B)
        {
            iNo1 = A;
            iNo2 = B;
        }

        int Addition()      //behaviour
        {
            int iAns = 0;
            iAns = iNo1 + iNo2;
            return iAns;
        }

        int Substraction()
        {
            int iAns = 0;
            iAns = iNo1 - iNo2;
            return iAns;
        }
};

int main()
{
    Arithmatic aobj(11,10);

    int iRet = 0;

    iRet = aobj.Addition();
    cout<<"Additon is :"<<iRet<<"\n";

    iRet = aobj.Substraction();
    cout<<"Substraction is :"<<iRet<<"\n";

    return 0;
}


//What you want to do
//How to do

