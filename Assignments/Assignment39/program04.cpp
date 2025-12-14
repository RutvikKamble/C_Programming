//  Write a recursive program to display below pattern
//  A   B   C   D   E   F

#include<iostream>
using namespace std;

void Display()
{
    static int iCnt = 0;
    static char ch = 'A';

    while(iCnt < 6)
    {
        cout<<ch<<"\t";
        iCnt++;
        ch++;
        Display();
    }
}

int main()
{
    Display();    

    return 0;
}
