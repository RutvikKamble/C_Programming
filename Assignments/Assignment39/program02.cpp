//  Write a recursive program to display below pattern
//  1   2   3   4   5

#include<iostream>
using namespace std;

void Display()
{
    static int iCnt = 1;

    while(iCnt <= 5)
    {
        cout<<iCnt<<"\t";
        iCnt++;
        Display();
    }
}

int main()
{
    Display();    

    return 0;
}
