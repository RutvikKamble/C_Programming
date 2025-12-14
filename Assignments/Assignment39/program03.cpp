//  Write a recursive program to display below pattern
//  5   4   3   2   1

#include<iostream>
using namespace std;

void Display()
{
    static int iCnt = 5;

    while(iCnt > 0)
    {
        cout<<iCnt<<"\t";
        iCnt--;
        Display(); 
    }
}

int main()
{
    Display();    

    return 0;
}
