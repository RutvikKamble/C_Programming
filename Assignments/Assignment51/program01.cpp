// Program which accepe one Value and one number from user,
// print that values that number of time on screen

#include<iostream>
using namespace std;

template<class T>
void Display(T value, int iSize)
{
    int iCnt = 0;

    for(iCnt = 1; iCnt <= iSize; iCnt++)
    {
        cout<<value<<"\t";
    }
    cout<<"\n\n";
}

int main()
{
    
    Display('R',5);
    Display(11,9);
    Display(3.4,3);
    

    return 0;
}