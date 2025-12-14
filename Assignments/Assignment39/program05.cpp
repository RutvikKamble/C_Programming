//  Write a recursive program to display below pattern
//  a   b   c   d   e   f

#include<iostream>
using namespace std;

void Display()
{
    static int iCnt = 0;
    static char ch = 'a';

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
