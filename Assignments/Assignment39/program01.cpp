//  Write a recursive program to display below pattern
//  *   *   *   *   *

#include<iostream>
using namespace std;

void Display()
{
    static int iCnt = 1;

    while(iCnt <= 5)
    {
        cout<<"*\t";
        iCnt++;
        Display();
    }
}

int main()
{
    Display();    

    return 0;
}