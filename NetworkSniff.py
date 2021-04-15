from scapy.all import *
import platform
import os
import argparse

error_code_unknown_platform = 1

# tracking options specified by user
payload = False
loopback_sniff = False
file_directory = os.getcwd() + "\PacketData"
file_name = None
port = 0

# used to troubleshoot
verbose = False


def main():
    if not os.path.exists(file_directory):
        os.makedirs(file_directory)
    parse_arguments()
    if loopback_sniff:
        if verbose:
            print("sniff loopback")
        sniff_loopback_port_packets()


def sniff_loopback_port_packets():
    sniffer_filter = "port " + str(port)
    interface = detect_loopback_interface()
    if interface == error_code_unknown_platform:
        print("Error finding OS aborting sniffing on loop back address")
        return
    if verbose:
        print("Sniffing local packets on port " + str(port))
    sniff(filter=sniffer_filter, iface=interface, prn=analyse_packet)


def analyse_packet(pkt):
    global payload
    if verbose:
        print("Packet found")
    packet = pkt.show(dump=True)
    if file_name is not None:
        file = open(file_directory + "\\" + file_name, "a")
        if payload:
            if Raw in packet:
                load = packet[Raw].load
                file.write(load)
        else:
            file.write(packet)
        file.close()
    #print(packet)


def detect_loopback_interface():
    print(str(platform.system()))
    if platform.system() == "Linux":
        return "lo"
    if platform.system() == "Windows":
        return "Software Loopback Interface 1"
    return error_code_unknown_platform


def parse_arguments():
    print("Parsing arguments")
    parser = argparse.ArgumentParser()
    parser.add_argument("-pay", "--payload", help="Specify to examine only the payload of the packet")
    parser.add_argument("-f", "--file", help="Output file name")
    parser.add_argument("port", help="Port to sniff data", type=int)
    parser.add_argument("-l", "--local", help="Designate to sniff on loop back address usually 127.0.0.1")
    parser.add_argument("-v", "--verbose", help="Console messages useful for debugging")
    args = parser.parse_args()
    global port
    port = args.port
    if port < 0:
        print("Port must be a positive value")
        sys.exit(0)
    if args.payload:
        global payload
        payload = True
    if args.file:
        global file_name
        file_name = args.file + ".txt"
    if args.local:
        global loopback_sniff
        loopback_sniff = True
    if args.verbose:
        global verbose
        verbose = True


if __name__ == "__main__":
    main()
